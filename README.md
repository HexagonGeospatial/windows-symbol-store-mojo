# windows-symbol-store-mojo
Maven plugin to wrap Microsoft's symstore.exe commands

[![Build Status](https://travis-ci.org/christapley/windows-symbol-store-mojo.svg?branch=master)](https://travis-ci.org/christapley/windows-symbol-store-mojo) [![Coverage Status](https://coveralls.io/repos/github/christapley/windows-symbol-store-mojo/badge.svg?branch=master)](https://coveralls.io/github/christapley/windows-symbol-store-mojo?branch=master)
---
[symstore.exe information](https://msdn.microsoft.com/en-us/library/windows/desktop/ms681417(v=vs.85).aspx)

Why might you need to set up a symbol server?
* Has QA ever given you a collection of mini dumps from unknown builds and you've spent most of a day searching for matching symbols? 
* Do you produce a multitude of builds but don't know where to put pdb files?
* Do you get crash/process dumps from clients or other intenal teams when something goes wrong?

If you answered yes to any other the above questions, you probably should setup a symbol server.  Besides, these days all the cool kids store their symbols from their msvc builds on a Microsoft Symbol Server.

The server itself isn't anything special, a UNC path that your build machines can write to is enough.

**_Remember that symstore.exe does not support symultaneous transactions from many instances and so you will need to accomodate this in your build system_**

This plugin attempts to wrap the common symstore.exe commands in maven mojo's for people who like to use maven for this sort of stuff.

Obviously, this plugin will only work on Windows.

## Examples

### Add Files
```xml
<plugin>
	<groupId>org.tapley</groupId>
	<artifactId>windows-symbol-store</artifactId>
	<version>1.0.0-SNAPSHOT</version>
	<configuration>
		<symStorePath>C:\Program Files (x86)\Windows Kits\10\Debuggers\x64\symstore.exe</symStorePath>
		<repositoryPath>C:\temp\symbols</repositoryPath>
		<applicationName>test</applicationName>
		<fileSets>
			<fileSet>
				<directory>C:\Temp\binaries</directory>   
				<includes>
					<include>*.dll</include>
				</includes>
			</fileSet>
		</fileSets>
	</configuration>
	<executions>
		<execution>
			<id>mojo-AddSymbols</id>
			<phase>process-sources</phase>
			<goals>
				<goal>AddSymbols</goal>
			</goals>
		</execution>
	</executions>
</plugin>
```