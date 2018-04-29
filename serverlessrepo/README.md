# PlantUML Serverless

An API Gateway + Lambda service for rendering PlantUML diagrams. 

Try here before you deploy: <https://plantuml.nitorio.us>

To try your own deployment, see the stack outputs after deploying for UI and example diagram links.

A basic UI is provided at the root path: https://{apigw endpoint}/plantuml/.

## Drop in replacement for official PlantUML server

This can be used as a drop in replacement for scenarios
where http://www.plantuml.com/plantuml is used a a rendering endpoint. You can avoid sending the diagram source to a server outisde your control and use an encrypted HTTPS endpoint for the diagram traffic.

This doesn't support everything the official PlantUML server does but should be good for most intents and purposes (PNG, SVG and TXT rendering).

For example, to have Visual Studio Code PlantUML plugin render using your own serverless deployment, set the following properties in vscode for the plugin: 

```json
    "plantuml.render": "PlantUMLServer",
    "plantuml.server": "https://your-endpoint-here"
```

Use `"plantuml.server": "https://plantuml.nitorio.us"` if you'd like to try before you deploy your own.

## Details for API usage

The plantuml text to render needs to be encoded as described here: <http://plantuml.com/pte>. The UI does the encoding and you can see the encoded source in the URL for the rendered diagram. Also, have a look at <https://github.com/markushedvall/node-plantuml> to encode with a CLI.

Make requests like this: https://{apigw endpoint}/png/{encoded plantuml}

## Example Diagrams

Example PNG diagram at <https://plantuml.nitorio.us/png/SyfFKj2rKt3CoKnELR1Io4ZDoSa70000>: 

![Example PNG diagram](https://plantuml.nitorio.us/png/SyfFKj2rKt3CoKnELR1Io4ZDoSa70000 "Example PNG diagram")

SVG format is also supported: <https://plantuml.nitorio.us/svg/SyfFKj2rKt3CoKnELR1Io4ZDoSa70000>

![Example SVG diagram](https://plantuml.nitorio.us/svg/SyfFKj2rKt3CoKnELR1Io4ZDoSa70000 "Example SVG diagram")

Made with ❤️ by [@mmajis](https://twitter.com/mmajis) at [@NitorCreations](https://twitter.com/NitorCreations). Available on the [AWS Serverless Application Repository](https://aws.amazon.com/serverless)

## License

GNU General Public License v3.0 only (GPL-3.0)