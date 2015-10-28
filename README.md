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
General configuration
* configure SSL port: ZAP -> Tools -> Options... -> SNI Terminator
* configure browser to use proxy or use it as transparent proxy

Root certificate:
* Generate root certificate: ZAP -> Tools -> Options -> Dynamic SSL Certificates -> Generate
* Export root certificate: ZAP -> Tools -> Options -> Dynamic SSL Certificates -> Save -> ``owasp_zap_root_ca.cer``

Import certificate in Firefox:
* open <a href="about:preferences#advanced">about:preferences#advanced</a>
* follow Certificates -> View Certificates -> Authorities -> Import ... 
* browse for exported certificate, e.g., ``owasp_zap_root_ca.cer``


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
