@echo off
set workingDir=file:///C:/Desenvolvimento/Java/Workspaces/Vivo-Arquitetura/wikiToPdf/
set toolDir="C:\Program Files (x86)\wkhtmltopdf\"
set wikiUrl=http://code.google.com/p/crux-framework/wiki/

%toolDir%wkhtmltopdf.exe --book --cover %workingDir%cover/cover.html %wikiUrl%UserManual UserManual.pdf
%toolDir%wkhtmltopdf.exe --book --cover %workingDir%cover/cover.html %wikiUrl%Widgets Widgets.pdf
%toolDir%wkhtmltopdf.exe --book --cover %workingDir%cover/cover.html %wikiUrl%BuildingCrux BuildingCrux.pdf
%toolDir%wkhtmltopdf.exe --book --cover %workingDir%cover/cover.html %wikiUrl%UsingDataSources UsingDataSources.pdf
%toolDir%wkhtmltopdf.exe --book --cover %workingDir%cover/cover.html %wikiUrl%WidgetDeveloperManual WidgetDeveloperManual.pdf
%toolDir%wkhtmltopdf.exe --book --cover %workingDir%cover/cover.html %wikiUrl%FAQ FAQ.pdf
