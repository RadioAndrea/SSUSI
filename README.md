----------------
----------------
SSUSI Auroral Area Map Rendering
----------------
----------------


Building:
----------------
This project can be built with most java compilers. The pre-compiled JAR files are compiled with Java 1.8, however the code is compatible with Java 1.7. No earlier versions have been tested.
This program creates renderings of the auroral information from the SSUSI instrument on DMSP f16 - f18 based on the data released in the netCDF format. 

Usage:
----------------
From the program window, select the directory with the data of interest, then press the "Generate Maps" button. The individual files can then be browsed with the "Next" and "Back" buttons. 

Interesting images can be exported in PNG format via the "Save Image" button. 

Individual layers within the netCDF data files can be viewed by selecting the appropriate layer number from the drop-down box. Layers 3 and 4 are LBHL and LBHS, respectively (probably). 

The top drop-down box under the button labeled "Choose Directory" is used to select which geographic pole is of interest. 

The "Magnetic Map" button displays a visualization of a 24 hour magnetic clock, with the border between the colors grey and green being magnetic midnight, and the other colors in the direction of the green wedge increasing in increments of 3 hours. That is to say, the border between green and grey is 0, and each traversal over a wedge adds or subtracts 3 hours.

In the settings menu, the size of each color's bucket can be changed. This "bucket" is the number of values that a single RGB value will represent. A smaller bucket size will allow greater fidelity in the conversion from numerical representation to visual color representation.

Bugs and Feature Requests:
----------------
Place a bug report or feature request in the appropriate location on this project's github page.

Legal:
----------------
This software released under Creative Commons 4.0 Share-Alike Attribution License.
Human readable summary here: http://creativecommons.org/licenses/by-sa/4.0/

Created using libraries provided by UNIDATA.
Unidata license:
Portions of this software were developed by the Unidata Program at the University Corporation for Atmospheric Research.

Access and use of this software shall impose the following obligations and understandings on the user. The user is granted the right, without any fee or cost, to use, copy, modify, alter, enhance and distribute this software, and any derivative works thereof, and its supporting documentation for any purpose whatsoever, provided that this entire notice appears in all copies of the software, derivative works and supporting documentation. Further, UCAR requests that the user credit UCAR/Unidata in any publications that result from the use of this software or in any product that includes this software, although this is not an obligation. The names UCAR and/or Unidata, however, may not be used in any advertising or publicity to endorse or promote any products or commercial entity unless specific written permission is obtained from UCAR/Unidata. The user also understands that UCAR/Unidata is not obligated to provide the user with any support, consulting, training or assistance of any kind with regard to the use, operation and performance of this software nor to provide the user with any updates, revisions, new versions or "bug fixes."

THIS SOFTWARE IS PROVIDED BY UCAR/UNIDATA "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL UCAR/UNIDATA BE LIABLE FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE ACCESS, USE OR PERFORMANCE OF THIS SOFTWARE. 
