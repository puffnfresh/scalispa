Scalispa
========

A small S-expression to JVM bytecode compiler.

Compiling
---------

You'll have to manually resolve dependencies, the two JARs are:

* asm-all-4.0_RC1.jar
* jitescript-0.2.1-SNAPSHOT.jar

You should be able to download them from the respective URLs:

* http://forge.ow2.org/projects/asm/
* https://github.com/qmx/jitescript

And then put them into a directory called `lib`.

Usage
-----

    $ sbt
    > run (+ 10 (* 5 5))
    > exit
    $ java Lisp
    35
