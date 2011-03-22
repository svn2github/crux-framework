#!/bin/sh
java -cp ./:./lib/build/crux-dev-deps.jar:./lib/build/crux-dev.jar:./lib/build/gwt-dev.jar:./lib/build/gwt-user.jar br.com.sysmap.crux.tools.projectgen.CruxProjectGenerator "$@";
