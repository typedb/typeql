# JetBrains-Graql-Plugin

Basic support for the Graql language (http://grakn.ai) on JetBrains-based IDEs

## Installation

This plugin is published on the [JetBrains Plugin Repository](#todo):

    Preferences → Plugins → Browse Repositories → Search for "Graql"

### From Source

Clone this repository:
```bash
$ git clone https://github.com/graknlabs/grakn
$ cd graql/plugins/jetbrains-plugin
```

Build the plugin zip file:

```bash
$ ./gradlew buildPlugin
```

Install the plugin from `./build/distributions/Jetbrains-Graql-Plugin-*.zip`:

    Preferences → Plugins → Install plugin from disk

## Development

Execute an IntelliJ IDEA instance with the plugin you're developing installed:

```bash
$ ./gradlew runIdea
```

Run the tests:

```bash
$ ./gradlew test
```

## Information

For more information about how this plugin works and how to add additional functionality please see:
 - https://www.jetbrains.org/intellij/sdk/docs/basics.html
 - https://www.jetbrains.org/intellij/sdk/docs/tutorials/custom_language_support_tutorial.html