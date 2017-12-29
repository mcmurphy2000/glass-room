# Glass Room Bin Packing

### How this project was started in Eclipse
- If you're using regular Eclipse instead of Spring STS, install plugins:
	- Help -> Eclipse Marketplace
	- Find: Spring
		- Spring IDE
		- Spring Tool Suite (STS) for Eclipse
		- SpringSource Tool Suite (STS) for Eclipse

- Start new Spring boot project
	- File -> New -> Project -> Spring -> Spring Starter Project
	- check Web in Web

### If you have cloned this project from git	
- Edit application-local.properties for DB URL, username and password
- Crate tables manually or set spring.jpa.hibernate.ddl-auto=create
	- tables `settings` always contains only one row with id=1
	- table `rect_history` is only used for logging
	
### Installing 2D-Bin-Packing
1. Clone 2D-Bin-Packing (or unpack `2D-Bin-Packing-master.zip`)
2. Delete all files in 2D-Bin-Packing\src\main\resources to save space
3. Install to local maven repo: `mvn clean install`

### Profiles
- There are 2 profiles in this app: local and heroku
	Profiles determine which application.properties files will be used
- Profile examples: http://www.baeldung.com/spring-profiles
- spring.profiles.default can be used to set default profile
	- If spring.profiles.active is set (which is being done in Heroku's Maven plugin), then its value determines which profiles are active.
	- But if spring.profiles.active isn’t set, then Spring looks to spring.profiles.default
	- When spring.profiles.active is set, it doesn’t matter what spring.profiles.default is set to; the profiles set in spring.profiles.active take precedence.

### Compile, run, deploy
- To compile (skipTests is necessary to prevent it connecting to DB, because you're not using any profile here):
`mvn clean package -DskipTests`
	
- To run locally:
`java -Dserver.port=8090 -Dspring.profiles.active=local -jar glassroom-0.0.1-SNAPSHOT.jar`
OR 
`mvn spring-boot:run -Drun.jvmArguments="-Dspring.profiles.active=local"`
	- setting `spring.profiles.active=local` will make Spring use `application-local.properties`
	- when it is being run on Heroku, heroku-maven-plugin in pom.xml uses this instead: `-Dspring.profiles.active=heroku`
	- alternatively, you can set OS System Environment variable `SPRING_PROFILES_ACTIVE=local`
	for more info see:
	https://docs.spring.io/spring-boot/docs/current/reference/html/howto-properties-and-configuration.html
	https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-external-config
	
- To run in Eclipse:
	- Run -> Run Configurations... -> Spring Boot App -> <glass-room> -> Spring Boot tab -> Profile = local
	- OR set system property spring.profiles.active=local

- To deploy:
`mvn clean heroku:deploy -DskipTests`

- To see logs:
`heroku logs --app APP-NAME --num NUMER_OF_LINES`

## Design
### For demo's sake:
- `rectWrapperSession` is a session scoped **model attribute**
- `rectWrapper` is a request scoped model attribute
	- on every request method `index()` takes `rectWrapperSession` and puts it in a model as a form backing object `"rectWrapper"`
	- on every post from a form, `showLayout()` accepts this backing object `"rectWrapper"` back, modifies it and puts it as `"rectWrapperSession"`  
	it takes `"rectWrapper"` instead of `"rectWrapperSession"` from the form because of the way I use JavaScript to modify rows of input in the form.  
	If it was taking directly to `"rectWrapperSession"`, the rows deleted by JavaScript would still remain in `"rectWrapperSession"`
- `settingsWrapper` is a session scoped **bean** managed by Spring. It is created in AppConfig class each time new session starts. 
	- SettingsWrapper bean will be loaded on each new session and will stay until session ends
	- I'm using SettingsWrapper instead of just Settings because otherwise, when Settings is @Autowired it's impossible to save it via
	settingsService.save(settings) (because it would be a Spring proxy, not an entity)
- `BinContainer` is an attribute stored in **HttpSession using traditional way**
	- method getImageAsResponseEntity() gets it using `@SessionAttribute` on a parameter

### Flow
- Important interface:
```
interface BinContainer {
	int getBinCount();
	BufferedImage getBinImage(int binIndex);
}
```

- Controller
	- asks service to store `List<Rect>`
	- creates new BinContainer object from (`List<Rect>`, settings)
	- stores BinContainer into **HttpSession**
	
- ImageResource method getImageAsResponseEntity()
	- retrieves BinContainer from **HttpSession** via `@SessionAttribute` on a parameter
	
### Futher search for bin-packing alternative library
- Python lib: https://github.com/secnot/rectpack
	- Can it be run via Jython? http://www.jython.org/jythonbook/en/1.0/JythonAndJavaIntegration.html#using-jython-within-java-applications
- JavaScript: https://github.com/jakesgordon/bin-packing (also check: https://github.com/bryanburgers/bin-pack , https://www.npmjs.com/package/bin-pack)
- Java compiled classes: http://cgi.csc.liv.ac.uk/~epa/surveyhtml.html
	- possibly use http://jd.benow.ca/
	- seems like only Strip Packing is implemented there; no rotation
- Another Java implementation: https://github.com/papuja/2DPackingAlgorithmDemo
	- seems like only Strip Packing
- Another Java implementation: https://github.com/Bartvhelvert/Rectangle-packing
	- no docs
	- GUI
	- unclear
- Another Java compiled classes: https://www.computational-logistics.org/orlib/topic/Space%20Defragmentation%20for%20Packing%20Problems/index.html
- Online solver: http://www.packit4me.com/
- 2D cutting stock problem Java implementations:
	https://github.com/achaussende/tp-2D-cutting-stock-problem
	https://github.com/DuncanvR/2dcuttingstock	(uses external lib, compiled and run it - sometimes it hangs or takes too long to calculate)
	https://github.com/Polytech-AdrienCastex/2D-Cutting-Stock-Problem-with-Setup-Cost - output varies, unclear
- French Web services for the bin packing problem (Java): https://www.isima.fr/~lacomme/ORWebServices/index.php?idx=7
- A look at 2D bin packing with Optaplanner: https://github.com/MichaelGoff/bin-packing