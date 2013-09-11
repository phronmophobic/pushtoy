# lein-pushtoy

A Leiningen plugin to make deploying clojure apps to an existing servers simple. Not meant for "production", but great for side projects.

## Usage

### Setup

Put `[lein-pushtoy "0.1.1-SNAPSHOT"]` into the `:plugins` vector of your
`:user` profile.

Add the following key to your project.clj file:

    $   :pushtoy {:ips ["<first ip>" "<second ip>"]
                  :user {:username "ubuntu"}]}

Options for your pushtoy config are:

* **:ips** - vector of deployment ip addresses
* **:user** - same keys that are available under pallet.core.user, http://palletops.com/pallet/api/0.8/pallet.core.user.html

   `:username` - username for sshing  
   `:public-key-path` - path string to public key file  
   `:private-key-path` - path string to private key file  
   `:public-key` - public key as a string or byte array  
   `:private-key` - private key as a string or byte array  
   `:passphrase` - passphrase for private key  
   `:password` - ssh user password  
   `:sudo-password` - password for sudo (defaults to :password)  
   `:sudo-user` - the user to sudo to  
   `:no-sudo` - flag to not use sudo (e.g. when the user has root privileges).  
   
   
   defaults to username being root and using private and public keys as ~/.ssh/id_rsa and ~/.ssh/id_rsa.pub

* **:app-name** - defaults to the name of the project


### Basic Usage

    $ lein pushtoy <command>...

Available commands:

* **install**: installs java, runit, and creates a service for your clojure application
* **deploy**: deploys your uberjar in a location that can be run from runit
* **start**: starts your deployed uberjar using runit
* **stop**: stops your application via runit
* **restart**: restarts your application via runit


### Initial deployment

    $ lein do uberjar, pushtoy install deploy restart

### Subsequent deployments

    $ lein do uberjar, pushtoy deploy restart

## Todo

- Add example plugin configurations
- Allow options for settings up nginx with virtual hosts
- Integrate vhost stuff with something like http://freedns.afraid.org/
- Integrate with tomcat
- Grace reloading without momentary deployment downtime


## License

Copyright © 2013 Adrian Smith

Distributed under the Eclipse Public License, the same as Clojure.
