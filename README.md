# tesseract4java: Tesseract GUI

## Forked from https://github.com/tesseract4java/tesseract4java
A graphical user interface for the [Tesseract OCR engine][tesseract].
[tesseract]: https://github.com/tesseract-ocr/tesseract



## Building and running the software

### This fork expects at least java 11

 This project uses submodules
 1. `git submodule init`
 2. `git submodule update`
 3. `mvn clean package -Pstandalone`. This will include the Tesseract binaries for your platform. You can manually
    define the platform by providing the option `-Djavacpp.platform=[PLATFORM]` (available platforms are
    `windows-x86_64`, `windows-x86`, `linux-x86_64`, `linux-x86`, and `macosx-x86_64`).

After you've run through all steps, the directory "gui/target" will contain the file
"tesseract4java-[VERSION]-[PLATFORM].jar", which you can run by double-clicking or executing
`java -jar tesseract4java-[VERSION]-[PLATFORM].jar`.

[Apache Maven]: https://maven.apache.org/

## Credits

  - This software uses the [Tesseract OCR engine][tesseract] ([APLv2.0]).
  - This software uses [ocrevalUAtion] by Rafael C. Carrasco for providing
    accuracy measures of the OCR results ([GPLv3]).
  - This software uses the [Silk icon set][silk] by Mark James
    ([famfamfam.com]) ([CC-BY-3.0]).

[APLv2.0]: http://www.apache.org/licenses/LICENSE-2.0
[GPLv3]: https://www.gnu.org/licenses/gpl-3.0.html
[ocrevalUAtion]: https://github.com/impactcentre/ocrevalUAtion
[silk]: http://www.famfamfam.com/lab/icons/silk/
[famfamfam.com]: http://www.famfamfam.com/
[CC-BY-3.0]: http://creativecommons.org/licenses/by/3.0/


## License

GPLv3

~~~
tesseract4java - a graphical user interface for the Tesseract OCR engine
Copyright (C) 2014-2019 Paul Vorbach

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
~~~
