#!/bin/sh
exec scala "$0" "$@"
!#

object Plugins extends Application {

  if(args.size > 1 && args(1) == "help")
  	help
  else if(args.size > 1 && args(1) == "available")
    executeAvailable
  else if(args.size > 1 && args(1) == "install")
    executeInstall
  else
  	executeList
  	
  def help = {
    p("[available|install|list|info]")
  }
  def executeAvailable = {
    p("Available Plugins:")
    p("- Security            (security:0.5)")
    p("- JPA Persistence     (jpa-persistence:2.0.4)")
    p("- Squeryl Persistence (squeryl-persistence:2.0.4)")
    p("- Static Content      (static-content:1.0.0)")
    p("- Social Media        (social-media:1.2.3)")
    p("- Breadcrumb          (breadcrumb:1.2.3)")
    p("- Spring Beans        (spring-beans:1.2.3)")
    p("- EJB Beans           (social-media:1.2.3)")
    p("- Search              (search:0.8.1)")
    end
  }
  def executeInstall = {
    p("Install")
  }
  def executeList = {
    p("List Installed:")
  }
  def p(str:String):Unit = println("  " + str)
  def end = println("")
}
Plugins.main(args)
