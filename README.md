# Glass Room Bin Packing

### Starting New Project
+ If you're using regular Eclipse instead of Spring STS, install plugins
	Help -> Eclipse Marketplace
	Find: Spring
		Spring IDE
		Spring Tool Suite (STS) for Eclipse
		SpringSource Tool Suite (STS) for Eclipse

+ Start new Spring boot project
	File -> New -> Project -> Spring -> Spring Starter Project
	check Web in Web
	
	
for demo's sake:
rectWrapperSession is session scoped model attribute
rectWrapper is a request scoped model attribute
	on every request method index() takes rectWrapperSession and puts it in a model as a backing object "rectWrapper"
	on every post from a form, showLayout() accepts this backing object "rectWrapper" back, modifies it and puts it as "rectWrapperSession"     
settings is session scoped bean managed by Spring
BinContainer is an attribute stored in HttpSession using traditional way