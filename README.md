# Brzy Web Application Framework

Brzy Webapp is tightly integrated with Fabricate build tool, Brzy Webapp is a modular action base
web framework that uses.  It’s design goals are to be simple, comprehensible, easily testable, and
extensible framework.

## Look Ma, no DSL
A controller with actions,  Actions are defined by a convention The actions must fulfill the
contract of the types of arguments it takes and response object it returns.  There is no DSL
to learn and the action methods are designed in a way that makes them easily comprehensible
and testable.

    class PersonController extends BaseController("persons") {
      override val actions = List( get("", index _, View("list"))
      def index = “persons”->Person.list
    }

In this example the url http://mysite.com/persons maps to the index action of the persons
controller.  The index action returns a tuple of a string and list of persons that will be
 set in request scope as the model to the view named list. The view name maps to the project
 file at the location ‘<project_root>/webapp/person/list.ssp’.

## Project Layout
The project structure is dependent upon the Fabricate build tool.

    <project root>
      +- brzy-webapp.b.yml
      +- src
      |  +- scala
      |
      +- test
      |  +- scala
      |
      +- target
      +- webapp

The brzy-webapp configuration file is the main configuration file for the entire project.
Src, test, target, and webapp should be self explanatory.  It’s important to note that there
are no other configuration files, other than the main brzy-webapp.b.yml file, no persistence.xml,
no logging.properties and nothing to manage under the WEB-INF directories.

See Configuration Reference for more detailed information about the details of the brzy-webapp.b.yml.

## Application Class
The application class is the main assembly point of the application.  There are two, one that
automatically discovers controllers and actions and one to manually assemble the application.

    class Application(c:WebAppConfiguration) extends WebApp(c) with JpaProvider {
      override val controllers = super.controllers ++ List( 
        new HomeController
      )
    }

## RESTful Action Path
Path expressions represent the restful path the corresponds to an action.  This makes for
easily bookmark-able and highly customizable application paths.

    class HomeController extends BaseController("home") {
      override val actions = List(
        get("", list _, View("/index"),
        post("{id:\\d+}", view _, View("view"))
      )
      def view(p:Parameters) = Model(“id”->p.url(“id”))
    }

An Action consists of:
 * The action path from the controller.
 * The request path from the base of the application context
 * The view to display.
 * A reference to the function that executes the action.
 * An varargs list of Constraints.

The request path http://mydomain.com/mycontext/home/123
would call the action and the function named view above.  Assuming your application is
deployed under the context name ‘mycontext’.  The action will call the view function,
automatically adding the parameters.  From the parameters you can access the parameters
embedded in the url, in this case, it’s called ‘id’.

Path attributes are wrapped in curly braces, and the values is set in the url scope
accessable in the Parameters action argument.  In the path attribute, of the action above,
it has the id parameter embedded in the url. It’s refined using the colon regular expression,
in this case, to say the id parameter can only be numbers.

This action has a default view of “view”, which maps to the <project root>/webapp/home/view.ssp
scalate server page file.

Calling the url http://mydomain.com/mycontext/home/abc would result in a 404.  Any path that
does not match a path expression will default to the containers default resource lookup.

Path expressions can also contain file expressions, the following action definition is valid.

    class FilesController extends BaseController("files") with Secured {
      override val actions = List( 
        get("{id}.gif", index _, View("/index"))
      )
      // ...
    }

## Constraints
Constraints are part of an action, and define how an action may be called.  Constraints are
added to the controller or appended to the end of the action.

## Ssl
Requires that the controller or action be called only when the connection is secured over ssl.

    class HomeController extends BaseController("") with Secured {
      override val constraints = Array(Ssl())
      override val actions = List(
        Action("", "/index", index _,Roles(“ROLE_ADMIN”))
      )
      // ...
    }

This sets all actions on the controller to be callable over ssl.

## ContentTypes
Defines what content types are allowed to call the action. It defaults to all.  The content
type is extracted from the accept content type header sent in the request.

    class HomeController extends BaseController("") with Secured {
      override val actions = List(
        get("", index _, View("/index"), ContentType(“text/json”))
      )
      def index(p:PostBody) = {
        val json = p.asJson
        Json(mapOfValues)
      }
    }

In this example, if the http servlet request type is not ‘text/json’ it will not be allowed
to call this action.

## HttpMethods
What http methods are allowed to call the action, It defaults to all.

    class HomeController extends BaseController("") with Secured with PersonStore {
      override val actions = List(
        get("{id}", save _, View("/save")),
        post("{id}", , save _, View("/save")),
        action("{id}", save _, View("/save")),
        async("{id}", asyncStart _)
      )
      def save(p:Parameters) = { 
        // ...
        (View(“view”),Model(“person”-> get(p.url(“id”))))
      }
    }

In the list of actions the one created with the get method only accepts GET requests, the post
only accepts POST and the action accepts any request menthod.  The action method can be refined by
adding an HttpMethods argument.

The action method async is special, it sets the actions as an async servlet that starts
an async connection to the client.  It must be a Http GET, and the method it references must return
an Async response type.

## Roles
What roles are allowed to call the action or controller, It defaults to all.

    class HomeController extends Controller("") with Secured {
      override val constraints = Seq(Roles(“ROLE_ADMIN”))
      override val actions = List( get("", index _, View("/index")))
      // ...
    }

To call any action in this controller, the authenticated user just posses the role ‘ROLE_ADMIN’
to be authorized to execute any action in this controller.  If they do not posses that role, the
user will get a http 403 disallowed error.

## Transaction
There are two transactions DefaultTransaction and NoTransaction.  Default will let you set
the transaction isolation and a read only flag, but it may or may not be implemented by the
underlying connection.

    class PersonController extends BaseController("persons") with Secured {
      override val transaction = DefaultTransaction(SERIALIZED)
      override val actions = List( get("", index _, View("/index")))
      // ...
    }
    
## Arg types
The type of arguments that an action can take are limited to the Traits listed below.  They
are traits so that they can easily be implemented for unit testing.

### Parameters
The parameters argument trait is the most commonly used action argument.  It makes the
attributes of the application, session, header, request and url accessible to the action.
All attributes are read only.

    class HomeController extends BaseController("") {
      override val actions = List( get("", "/index", index _))
      def action(p:Parameters) = {
        val obj = p(“reference”)
        val sessionObj = p.session match {
          case Some(s) => s(“name”)
        }
      }
    }

### Properties
Properties can be used to access some less frequently used attributes of the servlet
request object itself, like remoteHost.

    class HomeController extends BaseController("") {
      override val actions = List( get("", "/index", index _))
      def action(p:Parameters,pr:Properties) = {
        val remoteHost = pr.remoteHost
        Model(“name”->”value”)
      }
    }

### PostBody
This arg is used for file, xml, or json parsing of the request.  For files, it will make any
other parameters accessible from the request, since the http input stream can only be read once.

Note that apache upload is used to parse the request but is not a dependency by default,
you’ll have to include it yourself.  Without it you may see some strange compiler errors
even if you are only parsing the response as xml or json and not uploading a file.

Another caveat to it’s usage, is that when combined with the Parameters action argument,
request scope attributes will not be accessible from the parameters object.


    class HomeController extends BaseController("") {
      override val actions = List( post("", "/index", action _))
      def action(p:PostBody) = {
        val fileItem = p.paramAsFile(“file”)
        Model(“name”->”value”)
      {
    }

### Principal
Used by authentication, this holds the user role names and user name of the authenticated
user.  It also has a flag to indicate if there is an authenticated user present.

    class HomeController extends BaseController("") {
      override val actions = List( get("", "/index", index _))
      def action(p:Parameters,pr:Principal) = {
        val person = Person.findByUserName(pr.name)
        //...
      }
    }

### Cookies
This arg is used to read any cookies in the request.  It holds a list of all available cookies.

    class HomeController extends BaseController("") {
      override val actions = List( get("", "/index", index _))
      def action(p:Parameters,c:Cookies) = {
        val cookies = c.cookies
        Model(“name”->”value”)
      }
    }


## Response Types
There are two kinds of response types, Data and Direction.  Only one direction can be returned,
but multiple Data responses can be returned depending on the type of direction.
A tuple of type (String-> AnyRef) that will be set as the Model to the view.
A Response object.  The types are described below.
Tuple of 2 to 5 response objects.

Tuple of String, AnyRef
You may simply return a tuple, that will be interpreted as a single name value pair to be placed
in the model in request scope.

    class HomeController extends BaseController("") {
      override val actions = List( get("", "/index", action _))
      def action(p:Parameters) = “name”->p(“someParameter”)
    }


### Session
A Data response object.

    class HomeController extends BaseController("") {
      override val actions = List( get("", "/index", index _))
      def action(p:Parameters) =
        (Model(“name”->”value”),Session(“name”->”value”))
    }

the session have a couple of ways to construct it.  The most details is:

Session(add=List(“n”->”v”),remove=Array(“name”),invalidate=false)
Session.invalidate()
Session.remove(“name”,”otherName”)

### Model
A Data response object.  The  model is probably the most common response type.  It takes multiple
tuples and adds them as name value pairs to the request scope to make them available for
rendering in views.

    class HomeController extends BaseController("") {
      override val actions = List( get("", "/index", action _))
      def action(p:Parameters) = Model(“name”->”value”)
    }

### View
A Direction response object.

    class HomeController extends BaseController("") {
      override val actions = List( get("", "/index", action _))
      def action(p:Parameters) = (Model(“name”->”value”),View(“other”))
    }

In this example, the action view is set to index, but the action overrides it with the value
‘other’.  the action is prefixed with a slash so it’s relative to the controller path.  So
the actual views that will be called in this case is ‘/home/other.ssp’.  If there the view
response object was missing from this example the view that would be rendered would be ‘/index.ssp’.

### Redirect
A Direction Response object sends a 302 redirect to the client, with the context relative path.

    class HomeController extends BaseController("") {
      override val actions = List( get("", "/index", action _))
      def action(p:Parameters) = Redirect(“/home/view”)
    }

### Forward
A Direction Response object will transparently forward directly to another controller within
the same transaction.

    class HomeController extends BaseController("") {
      override val actions = List(
        Action("", "/index", action _),
        Action("{id}", "/view", view _)
      )
      def action(p:Parameters) = Forward(“/home/123”)
      def view(p:Parameters) = Model(“name”->”value”)
    }

### Json
A Direction Response object that will convert an object or a map to a json data structure and
set the content type header to ‘text/json’

    class HomeController extends BaseController("") {
      override val actions = List( get("", "/index", action _))
      def action(p:Parameters) = {
        val someMapOfValues = Map.empty[String,String]
        Json(someMapOfValues)
      }
    }

With the json object you can add your own custom parser by overriding org.brzy.webapp.action.Parser
or mixing in your own.

    trait MyParse extends Parser { self:Json =>
      override def parse = “”
      override def contentType = “text”
    }
    class MyController extends BaseController("") {
      // ...
      def action(p:Parameters) = new Json(data) with MyParser
    }

### Jsonp
A Direction Response object is much like the Json response type but it wraps the data within a
javascript method call and returns the call as javascript.

    class HomeController extends BaseController("") {
      override val actions = List( get("", "/index", action _))
      def action(p:Parameters) = Jsonp(“call”,map)
    }

You can also extend this one with the parse in the same fashion as the example above.

### Xml
A Direction Response object returns xml in the response with the content type set to ‘text/xml’.

    class HomeController extends BaseController("") {
      override val actions = List( get("", "/index", action _))
      def action(p:Parameters) = Xml(Map(“name”->”value”))
    }

### Stream
A Direction Response object can stream data in the response.  The is the preferred way to
download files.

    class HomeController extends BaseController("") {
      override val actions = List( get("", "/index", index _))
      def action(p:Parameters) = Stream({io:OutputSeam =>
        io.write(byte)
      },“type”)
    }

### Binary
A Direction Response object is similar to stream, it returns a byte array to the output stream.
Generally Stream is peferable, but this can be used for small files.

    class HomeController extends BaseController("") {
      override val actions = List( get("", "/index", action _))
      def action(p:Parameters) = Binary(data,“application/pdf”)
    }

### Text
A Direction Response object is suitable for returning plain text as the response.

    class HomeController extends BaseController("") {
      override val actions = List( get("", "/index", action _))
      def action(p:Parameters) = Text(str)
    }

### Async
The Async return type can only be returned from mentods that are reference in async
actions.  There are three parameters,

 *  A function to execute in a async runnable thread.
 *  An int timeout which default to zero (never).
 *  An AsyncContextListener which defaults to a blank listener.

    class HomeController extends BaseController("") {
      override val actions = List( async("async", startAsync _, View("")))
      def startAsync(p:Parameters) = Async({(params)=>{
        // listen for some evens here, then return to client
        Json(Map("name"->"value"))
      }})

    }


### Error
A Direction Response object is used in many cases where an action returns an error.

    class HomeController extends BaseController("") {
      override val actions = List( get("", "/index", index _))
      def action(p:Parameters) = {
        if(somecheck())
          Model(“name”->”value”)
        else
          Error(500,”Error Message”)
      }
    }

### Flash
A Data Response object, this object gets places in session scope, and gets called once from
the view.  Once displayed it removes itself from the session.

    class HomeController extends BaseController("") {
      override val actions = List( get("", "/index", action _))
      def action(p:Parameters) =
          (Model(“name”->”value”),Flash(“code”,”default”))
    }

### Headers
A Data Response object, allows you to add http headers to the response.

    class HomeController extends BaseController("") {
      override val actions = List( get("", "/index", action _))
      def action(p:Parameters) =
          (Model(“name”->”value”),Headers(“cache”->”no_cache”))
    }

### Cookie
A Data Response object that adds a cookies to the response

    class HomeController extends BaseController("") {
      override val actions = List( get("", "/index", action _))
      def action(p:Parameters) =
          (Model(“name”->”value”),Cookie(“name”,”value”))
    }

## Action interceptor
A controller can intercept the call to an action.

    class HomeController extends BaseController("") with Intercepted {
      override val actions = List( get("", "/index", index _))
      override def intercept(a:()=>AnyRef, args:Array[Arg], p:Principal)={
        // do additional checks
        a()
      }
      def action(p:Parameters) = Model(“name”->”value”)
    }

In the example the ‘a’ argument is the action method wrapped within another action.  the args
are the arguments to the action, and the Principal is available for use for authorization.

## Securing Actions and Controllers
Authentication and authorization is built into the Brzy.  It consists of a few traits that must
be implemented by the Domain classes that represent the Authenticated user, the Authorities that
user possesses, the controllers that need authorization, and a service class that handles the
login and password encryption.

### Domain Classes
Person
The authenticated user domain class must implement the Identity trait.

### Authority
The authorities the user possesses.  This domain class does not require any traits, it is
trasparent to the authorization process.

### Security Service Class
A service class is necessary to handle encryption of passwords and authentication of the user.
The service you create must implement the Permission[T<:Authenticated] trait and injected where
needed in the Application class.

    class class SecurityService extends Permission[Person] with Service {
      val authenticator = Person
      def active(person:Person) = person.enabled
    }

### Controller Secure trait
Controllers are not authenticated unless they have the Secured trait.  With the secured trait,
the user is implicitly validated against the Roles Constraint on the controller and actions.

    class HomeController extends BaseController("") extends Secured{
      override val constraints = Array(Roles(“USER”,”ADMIN”))
      // ...
    }

### AuthController
There is a default AuthorizationController that can be used to authorize a user, or you may
create your own.  The default  one is org.brzy.webapp.controller.DefaultAuthController.

## Method interception
All controllers marked with the Secured trait have the interceptor trait included by default.
 It can be used to do more specific authorization.

## Views Scalate
Views are plug-able as well, and there is only one by default, Scalate.

Additional built in functions for Scalate
flash()
hasFlash()
i18n(s)
isAuthorized(roles:String*)
isLoggedIn()
resource(s)
res(s)
link(s)
action(s)
css(s)
js(s)
img(s)
date(d,t)
number(n,t)
encode(s) utf8

## Json
### Upload
Using the PostBody action argument you can read json from the request like this.

### Download
Using the Json response object, simple pass your class to it as an argument.

    class HomeController extends BaseController("home") {
      override val actions = List(
        Action("j", "", a _,HttpMethods(Post),ContentTypes(“text/json”))
      )
      def a(p:PostBody) = {
        val jsonData = p.asJson // list or map
        Json(Map(“name”->”vaue”))
      }
    }

## Xml
### Upload
Using the PostBody action argument you can read json from the request like this.

### Download
Using the Xml response object, simple pass your class to it as an argument.

    class HomeController extends BaseController("xml") {
      override val actions = List(
        Action("", "", act _, HttpMethods(Post),ContentTypes(“text/xml”))
      )
      def act(p:PostBody) = {
        val xml:Elem = p.asXml // scala xml element
        Xml(mapOrEntity)
      }
