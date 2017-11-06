# Glass Room Bin Packing

### Starting New Project
+ If you're using regular Eclipse instead of Spring STS, install plugins:
	+ Help -> Eclipse Marketplace
	+ Find: Spring
		+ Spring IDE
		+ Spring Tool Suite (STS) for Eclipse
		+ SpringSource Tool Suite (STS) for Eclipse

+ Start new Spring boot project
	+ File -> New -> Project -> Spring -> Spring Starter Project
	+ check Web in Web

### If you have cloned this project from git	
+ Rename src/main/resources/*.properties-dummy to *.properties
+ Edit application.properties for DB URL, username and password
+ Crate tables manually or set spring.jpa.hibernate.ddl-auto=create
	+ tables `settings` always contains only one row with id=1
	+ table `rect_history` is only used for logging
	
### Installing 2D-Bin-Packing
1. Clone 2D-Bin-Packing (or unpack `2D-Bin-Packing-master.zip`)
2. Delete all files in 2D-Bin-Packing\src\main\resources to save space
3. Install to local maven repo: `mvn clean install`

### Compile, run, deploy
+ To compile:
`mvn clean package`
	
+ To run locally:
`java -Dserver.port=8090 -jar glassroom-0.0.1-SNAPSHOT.jar`
or 
`mvn spring-boot:run`

+ To deploy:
`mvn clean heroku:deploy`

## Design
### For demo's sake:
+ `rectWrapperSession` is a session scoped **model attribute**
+ `rectWrapper` is a request scoped model attribute
	+ on every request method `index()` takes `rectWrapperSession` and puts it in a model as a form backing object `"rectWrapper"`
	+ on every post from a form, `showLayout()` accepts this backing object `"rectWrapper"` back, modifies it and puts it as `"rectWrapperSession"`  
	it takes `"rectWrapper"` instead of `"rectWrapperSession"` from the form because of the way I use JavaScript to modify rows of input in the form.  
	If it was taking directly to `"rectWrapperSession"`, the rows deleted by JavaScript would still remain in `"rectWrapperSession"`
+ `settingsWrapper` is a session scoped **bean** managed by Spring. It is created in AppConfig class each time new session starts. 
	+ SettingsWrapper bean will be loaded on each new session and will stay until session ends
	+ I'm using SettingsWrapper instead of just Settings because otherwise, when Settings is @Autowired it's impossible to save it via
	settingsService.save(settings) (because it would be a Spring proxy, not an entity)
+ `BinContainer` is an attribute stored in **HttpSession using traditional way**
	+ method getImageAsResponseEntity() gets it using `@SessionAttribute` on a parameter

### Flow
+ Important interface:
```
interface BinContainer {
	int getBinCount();
	BufferedImage getBinImage(int binIndex);
}
```

+ Controller
	+ asks service to store `List<Rect>`
	+ creates new BinContainer object from (`List<Rect>`, settings)
	+ stores BinContainer into **HttpSession**
	
+ ImageResource method getImageAsResponseEntity()
	+ retrieves BinContainer from **HttpSession** via `@SessionAttribute` on a parameter
