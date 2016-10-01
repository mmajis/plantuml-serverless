## Almost rendering PlantUML diagrams with AWS Lambda

This is a test to see how the [Lambada framework](https://github.com/lambadaframework/lambadaframework) works for API
development using AWS API Gateway & Lambda.

The idea was to create a serverless [PlantUML](http://plantuml.com) diagram rendering server, but it turns out that AWS API Gateway does not
support binary responses at this time. Also Lambada doesn't support configuring CORS headers...

Oh well.