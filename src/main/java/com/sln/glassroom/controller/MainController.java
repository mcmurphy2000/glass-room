package com.sln.glassroom.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sln.glassroom.binpacking.BinContainer;
import com.sln.glassroom.binpacking.BinContainerImpl;
import com.sln.glassroom.domain.Rect;
import com.sln.glassroom.domain.Settings;
import com.sln.glassroom.domain.SettingsWrapper;
import com.sln.glassroom.service.RectService;
import com.sln.glassroom.service.SettingsService;
import com.sln.glassroom.validator.RectWrapperValidator;
import com.sln.glassroom.view.FormSettings;
import com.sln.glassroom.view.RectWrapper;

@Controller
@SessionAttributes("rectWrapperSession")
public class MainController {
	
	private static final Logger LOG = LoggerFactory.getLogger(MainController.class);
	
	private static final Integer DEFAULT_ID = 1;
	private static final String DEFAULT_LABEL = "P1";
	private static final Integer DEFAULT_WIDTH = null;
	private static final Integer DEFAULT_HEIGHT = null;
	private static final Integer DEFAULT_QUANTITY = 1;
	private static final String DEFAULT_COLOR = "#1D7D85";
	
	@Autowired
	RectWrapperValidator rectWrapperValidator;
	
	@Autowired
	RectService rectService;
	
	@Autowired
	SettingsService settingsService;

	@Autowired
	SettingsWrapper settingsWrapper;
	
	@InitBinder("rectWrapper")
	protected void initBinder(WebDataBinder binder) {
		binder.addValidators(rectWrapperValidator);
	}
	
	// util method to extract client IP address
	private static String getClientIp(HttpServletRequest request) {
		String remoteAddr = "";
		if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr))
                remoteAddr = request.getRemoteAddr();
        }
        return remoteAddr;
	}
	
	// case scenario when this attribute is specified @SessionAttributes("rectWrapper") on a Controller class 
	// if the attribute is not in the session yet, this will be called and will add it to session
	// but once it is in the session, this will not be called anymore and will not overwrite it
	// see: http://www.logicbig.com/tutorials/spring-framework/spring-web-mvc/spring-model-attribute-with-session/
	@ModelAttribute("rectWrapperSession")
	public RectWrapper rectWrapper() {
		// if rectWrapper is in the session this method will not be called
		// if rectWrapper is not in the session, get it from service
		List<Rect> rectList = rectService.findAll();
		// if it contains 0 rows, then add 1 empty row because form needs it
		if (rectList.size() == 0)
			rectList.add(new Rect(DEFAULT_ID, DEFAULT_LABEL, DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_QUANTITY, DEFAULT_COLOR));
		
		RectWrapper rectWrapper = new RectWrapper();
		rectWrapper.setRectList(rectList);
		
		return rectWrapper;
	}

	@RequestMapping("/")
	public String index(@ModelAttribute("rectWrapperSession") RectWrapper rectWrapper, Model model) {
		model.addAttribute("rectWrapper", rectWrapper);
		return "index";
	}

	// "rectWrapperSession" is already present in the Model with session scope
	// but we're using "rectWrapper" because some rows might be absent in the form (deleted with JavaScript) and
	// we want new rectWrapper object to be created and bind data from form to it.
	// If we specified "rectWrapperSession" instead of "rectWrapper" here, then it would take already existing object and append/overwrite to it
	// with data from form, thus leaving possibly deleted rows intact
	// Generally, it seems like a bad idea to have a form backing object bind to already existing attribute
	@RequestMapping(value="/showLayout", method=RequestMethod.POST)
	public String showLayout(@ModelAttribute("rectWrapper") @Valid RectWrapper rectWrapper, BindingResult result, ModelMap model, HttpSession session, HttpServletRequest request) {
		// if some rows were removed in form using JavaScript, corresponding entries will still be present
		// in the list here. They will just be empty (isNew() = true)
		// That's why those rows are removed (filtered out) in the RectWrapperValidator
		// They are removed in Validator (and not here) because otherwise indexes in the list may get messed and will not match binding errors (if any)
		// Modifying list in Validator seems like a bad design but that's because JavaScript modifies HTML
		
		// Put "rectWrapperSession" into model again (overwriting the one that is already there with a new list)
		// "rectWrapperSession" may now contain some erroneous fields
		// so these erroneous fields will be available for the user until session ends,
		// but they will not be saved to database
		model.addAttribute("rectWrapperSession", rectWrapper);
		
		if (result.hasErrors()) {
			model.addAttribute("flashClass", "alert-danger");		// flash box
			model.addAttribute("flashMessage", "Error! Please correct fields shown in red.");
			return "index";
		}
		
		List<Rect> rectList = rectWrapper.getRectList();
		rectService.saveAll(rectList, getClientIp(request));
		
		// this is probably a bad design as I have to new the object myself
		// but I use it for the sake of demonstration of storing object into HttpSession
		Settings settings = settingsWrapper.getSettings();
		int margin = settings.getMinDistanceBetweenPieces() / 2;
		int binWidth = settings.getBedWidth();
		int binHeight = settings.getBedHeight();
		BinContainer binContainer = new BinContainerImpl(rectList, margin, binWidth, binHeight);
		session.setAttribute("binContainer", binContainer);	// saving "binContainer" to HttpSession using traditional way   
		model.addAttribute("binCount", binContainer.getBinCount());
		
		return "showLayout";
	}
	
	@RequestMapping("/settings/")
	public String settings(Model model) {
		// maybe use dozer here?
		FormSettings formSettings = new FormSettings();
		settingsService.assignFromSettings(formSettings, settingsWrapper.getSettings());
		model.addAttribute("formSettings", formSettings);
		return "settings";
	}
	
	@RequestMapping(value="/saveSettings", method=RequestMethod.POST)
	public String saveSettings(@ModelAttribute("formSettings") @Valid FormSettings formSettings, BindingResult result, ModelMap model, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			model.addAttribute("flashClass", "alert-danger");		// flash box
			model.addAttribute("flashMessage", "Error! Please correct fields shown in red.");
			return "settings";
		}
		
		Settings settings = settingsWrapper.getSettings();
		settingsService.assignFromFormSettings(settings, formSettings);
		settingsService.save(settings);

		redirectAttributes.addFlashAttribute("flashClass", "alert-success");	// flash box
		redirectAttributes.addFlashAttribute("flashMessage", "Your settings have been saved");	// flash box
		return "redirect:/";
	}
	
	@RequestMapping(value="/deleteAll", method=RequestMethod.POST)
	public String deleteAll(SessionStatus status) {
		rectService.deleteAll();
		status.setComplete();	// this will cause rectWrapper() method to be called again on next request and will update "rectWrapperSession"
		return "redirect:/";	// redirect won't have any effect because it's JavaScript who is posting here
	}

	
	// Returning Image/Media Data with Spring MVC
	// see: http://www.baeldung.com/spring-mvc-image-media-data
	//
	// ResponseEntity is an alternative to @ResponseBody. ResponseEntity is an object that carries metadata (such as headers and the status code)
	// ResponseEntity implies the semantics of @ResponseBody, so the payload will be rendered into the response body just as if the method were annotated with @ResponseBody.
	// But you need ResponseEntity if you want to set headers
	// There’s no need to annotate the method with @ResponseBody if it returns ResponseEntity.
	// (see 16.3.1 in "Spring in Action 4th Edition" book)
	//
	// "binContainer" was saved to HttpSession using traditional way, retrieving it using @SessionAttribute
	//
	// If I were not using HttpHeaders I could just return  @ResponseBody byte[] instead of ResponseEntity<byte[]>
	// (see page 435 in "Spring in Action 4th Edition" book)
	@RequestMapping(value = "/binimage/{index}", method = RequestMethod.GET)
	public ResponseEntity<byte[]> getImageAsResponseEntity(@SessionAttribute(value = "binContainer", required = false) BinContainer binContainer, @PathVariable("index") int index) {
		// page will ask for index from 1 to binCount, but binContainer.getBinImage() is 0 based
		if (binContainer == null || index < 1 || index > binContainer.getBinCount()) {
			//return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			throw new BinNotFoundException(index); 
		}
		
		// page will ask for index from 1 to binCount, but binContainer.getBinImage() is 0 based
		BufferedImage img = binContainer.getBinImage(index - 1);
		byte[] imageInByte = null;
	    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			ImageIO.write( img, "png", baos );
		    //baos.flush();
		    baos.close();
		    imageInByte = baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			LOG.debug(e.getMessage());
		}
		
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(new MediaType("image","png")); // needed?
	    headers.setCacheControl(CacheControl.noCache().getHeaderValue());
	    ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(imageInByte, headers, HttpStatus.OK);
	    
	    return responseEntity;
	}
	
	// see 7.3.2 in "Spring in Action 4th Edition" book
//	@ExceptionHandler(BinNotFoundException.class)
//	public String handleBinNotFound() {
//		return "error";
//	}

	// Because binNotFound() always returns an Error, the only reason to keep ResponseEntity around is so you can set the status code.
	// But by annotating binNotFound() with @ResponseStatus(HttpStatus.NOT_FOUND), you can achieve the same effect and get rid of ResponseEntity.
	// Again, if the controller class is annotated with @RestController, you can remove the @ResponseBody annotation and clean up the code a little more
	// see page 436 in "Spring in Action 4th Edition" book
	@ExceptionHandler(BinNotFoundException.class)
	//public ResponseEntity<Error> binNotFound(BinNotFoundException e) {
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public @ResponseBody Error binNotFound(BinNotFoundException e) {
		int index = e.getIndex();
		return new Error(4, "Bin [" + index + "] not found");
	}

}
