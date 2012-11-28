The problem
===========
A large part of performance testing, up to this point, has been on the server side of things.  However, with the advancement of technology, HTML5, JS and CSS improvements, more and more logic, behaviour, etc have been pushed down to the client.  Things that add to the overall browser execution time may include:

1. Client-side Javascript execution - eg. AJAX, JS templates
1. CSS transforms - eg. 2D, 3D, matrix transforms.
1. 3rd party plugins - eg. Facebook like, Double click ads, site analytics, etc

All these things add to the overall browser execution time, and this project aims to measure the time it takes to complete rendering all this content.  

**Note:** This project is **not** intended to replace the [HTTP Samplers](http://jmeter.apache.org/usermanual/component_reference.html#HTTP_Request) included in [JMeter](http://jmeter.apache.org/), rather it is meant to compliment them by measuring the end user load time.  The use case would **still** be to create the HTTP Samplers with 50, 100, etc virtual users to load test the server.  But at the same time we would also execute a separate JMeter test suite comprising of 1 or 2 Web Samplers which will control browser instances measuring the client side exeuction time.

Proposed  solution - JMeter with WebDriver
==========================================
As mentioned we will use [JMeter](http://jmeter.apache.org) to simulate the load creation and [WebDriver](http://seleniumhq.org) to create and control the browser interaction(s) with the server.  To script the actions for the browser, we currently use Javascript (provided by [Rhino](http://www.mozilla.org/rhino/)).  

See the [Getting Started](https://github.com/cplim/JMeter/wiki/Getting-Started) section to try out the spike.
