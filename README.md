# speclj-growl

[![Build Status](https://secure.travis-ci.org/pgr0ss/speclj-growl.png)](http://travis-ci.org/pgr0ss/speclj-growl)

speclj-growl is a plugin for [speclj](http://speclj.com/) that shows the success and failure messages with [Growl](http://growl.info/).

## Installation

Go to System Preferences -> Growl -> Network and check the following boxes:

* Listen for incoming notifications
* Allow remote application registration (make sure password field is empty)

If you use [leiningen](https://github.com/technomancy/leiningen), add the following to your project.clj:

    :dev-dependencies [[speclj-growl "1.0.0"]]

## Usage

Add `-f growl` to lein spec to show output in growl. For example, this will start autotest with both terminal and growl output:

    lein spec -a -f growl

This is short for:

    lein spec -r vigilant -f documentation -f growl

## License

Copyright (C) 2011 Paul Gross

Distributed under the The MIT License.
