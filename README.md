M101J Assignment 2.3
====================
This is the assignment for Homework 2.3 of the [M101J](https://education.10gen.com/) MongoDB online course translated to [Scala](http://www.scala-lang.org/) and [Play Framework](http://www.playframework.com/).

This project uses the official MongoDB Scala driver [Casbah](http://api.mongodb.org/scala/casbah/current/index.html).

The integration of Casbah into Play is done by the [MongoDB Salat Plugin](https://github.com/leon/play-salat) for the Play Framework.
We don't use the ORM features of this plugin, only the connection management.

The Assignment
--------------
As in the original Java + Spark version of the assignment, your task is to implement the missing data access logic
in `MongoUserDAO`. Look for occurences of `XXX WORK HERE` in [app/models/user.scala](app/models/user.scala).

In `MongoUserDAO`, you access the [MongoCollection](http://api.mongodb.org/scala/casbah/current/api/#com.mongodb.casbah.MongoCollection)
through the `users` private field.

You still need running mongod with the data provided by the official course's page. The only difference is the blog's web application.

Running the Play application and the validation script
------------------------------------------------------
After you're done with the implmentation you need to run the application and the validation script (`validate.py`) that
comes with the original version of this assignment.

The validation script expects the application to be running at `localhost:8082`:

	> play run 8082
	
Then run the validation script in a different terminal. If you implemented the required features correctly, the script
will provide a validation code that you will need for the assignment's submission form.

Side note
---------
This project uses the [Cake Pattern](http://jonasboner.com/2008/10/06/real-world-scala-dependency-injection-di/) to glue together the application - specifically the `BlogController` to the DAOs.
You don't actually need to understand the whole application, although I encourage you to dig into it.


Disclaimer
----------
This project is provided "as is". It has not been properly tested and there is no official support for it
provided by anyone (especially not by 10gen). It is intended for M101J's students interested in Scala and
it will most likely be harder to pass the course using this instead of the official Java + Spark version.

Last but not least: Do not publicly disclose solutions to the course's homeworks.
