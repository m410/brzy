#!/bin/sh
exec scala "$0" "$@"
!#
object HelloWorld extends Application {
    println("Hello, world! " + args.toList)

}
HelloWorld.main(args)
