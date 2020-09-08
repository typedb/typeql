<!--
Copyright (C) 2020 Grakn Labs

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
-->

# Graql Java API

## Package Structure

![Graql Java Package Structure](../docs/java-package-structure.png)

> To update the diagram above, run `docs/deps.sh //java/... java-package-structure` and it will regenerate the image using Graphviz (to install: `brew install graphviz`). Note that we ignore external dependencies (3rd party and `@graknlabs_*`), as well as the `//java/common` package in which every package depends on.
