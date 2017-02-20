# windows-symbol-store-mojo
Maven plugin to wrap Microsoft's symstore.exe commands

[![Build Status](https://travis-ci.org/christapley/windows-symbol-store-mojo.svg?branch=master)](https://travis-ci.org/christapley/windows-symbol-store-mojo) [![Coverage Status](https://coveralls.io/repos/github/christapley/windows-symbol-store-mojo/badge.svg?branch=master)](https://coveralls.io/github/christapley/windows-symbol-store-mojo?branch=master)
---
[symstore.exe information](https://msdn.microsoft.com/en-us/library/windows/desktop/ms681417(v=vs.85).aspx)

All the cool kids store their symbols from their msvc builds on a Microsoft Symbol Server.  This makes their life easier when QA or a customer gives them a crash dump and asks "what happened?".

The server itself isn't anything special, a UNC path that your build machines can write to is enough.

_Remember that symstore.exe does not support symultaneous transactions from many instances and so you will need to accomodate this in your build system_**

This plugin attempts to wrap the common symstore.exe commands in maven mojo's for people who like to use maven for this sort of stuff.