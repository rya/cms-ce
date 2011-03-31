
# Enonic CMS Community Edition

Welcome to the home of Enonic CMS Community Edition. Here you will find all source code for the product.

## Building

To build the project we are now using [Gradle](http://www.gradle.org). You can either install Gradle yourself
or use the included Gradle wrapper. To use the wrapper, call gradle with `./gradlew` (`./gradlew.bat` on Windows).

Build all code and run all tests (except integration tests):

    gradle clean build

Build all code including integration tests:

    gradle clean build integrationTest

## License

This software is licensed under AGPL 3.0 license. See full license terms [here](http://www.enonic.com/license). Also the distribution includes
3rd party software components. The vast majority of these libraries are licensed under Apache 2.0. For a complete list please 
read [NOTICE.txt](https://github.com/enonic/cms-ce/raw/master/NOTICE.txt).

	Enonic CMS
	Copyright (C) 2000-2011 Enonic AS.

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU Affero General Public License as
	published by the Free Software Foundation, either version 3 of the
	License, or (at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU Affero General Public License for more details.

	You should have received a copy of the GNU Affero General Public License
	along with this program.  If not, see <http://www.gnu.org/licenses/>.
