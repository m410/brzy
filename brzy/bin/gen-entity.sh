#!/bin/sh
exec scala "$0" "$@"
!#
object HelloWorld extends Application {
    println("Hello, world! " + args.toList)
		print("what's your name: ")
		val result = readLine()
		println("thanks " + result)
}
HelloWorld.main(args)
