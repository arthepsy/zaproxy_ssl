# zaproxy_ssl [![build status](https://travis-ci.org/arthepsy/zaproxy_ssl.svg)](https://travis-ci.org/arthepsy/zaproxy_ssl/)
This is **SSL termination plugin** for [OWASP Zed Attack Proxy (ZAP)](https://www.owasp.org/index.php/OWASP_Zed_Attack_Proxy_Project).

## Requirements
* ZAP 2.4.2+
* Java 8

## Installation
1. open ZAP
2. File -> Load Add-on file... 
3. browse for ``sniTerminator-*.zap`` file

## Usage
Tools -> Options... -> SNI Terminator

## Building
Install dependencies
```
./libs/install.sh
```
Build package
```
mvn package
```
After successful build, plugin will available in: ``sni-terminator-plugin/target/`` directory

## Misc
If you are using ZAP up till 2.4.0, then use _official_ "SNI Terminator" plugin from Add-on Marketplace.
