# zaproxy_ssl [![build status](https://travis-ci.org/arthepsy/zaproxy_ssl.svg)](https://travis-ci.org/arthepsy/zaproxy_ssl/)
This is **SSL/TLS termination plugin** for [OWASP Zed Attack Proxy (ZAP)](https://www.owasp.org/index.php/OWASP_Zed_Attack_Proxy_Project),
when it is being used as transparent (man-in-the-middle) proxy. Plugin takes advantage of TLS extension SNI (Server Name Indication).

## Requirements
* ZAP 2.4.2+
* Java 8

## Installation
1. build or [download plugin](https://github.com/arthepsy/zaproxy_ssl/releases/latest) 
2. open ZAP
3. File -> Load Add-on file... 
4. browse for ``sniTerminator-*.zap`` file

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

## ChangeLog
### v1.1 (2016-08-08, beta6)
- change certificate signature algorithm to SHA-256 with RSA encryption
- integrate pull requests from original sni-terminator (set thread as daemon)
- integrate changes from zap-extensions (languages, warning fixes)

### v1.0 (2015-10-28, beta4)
- bump dependency to BouncyCastle 1.52
- package for single source and single build
- import sni-terminator extension sources
- import sni-terminator sources

