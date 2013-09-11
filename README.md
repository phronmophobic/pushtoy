# lein-pushtoy

A Leiningen plugin to make deploying clojure apps to an existing servers simple.

## Usage

### Setup

Put `[lein-pushtoy "0.1.1-SNAPSHOT"]` into the `:plugins` vector of your
`:user` profile, or if you are on Leiningen 1.x do `lein plugin install
lein-pushtoy 0.1.0-SNAPSHOT`.

Add the following key to your project.clj file:

    $   :pushtoy {:ips ["<first ip>" "<second ip>"]
                  :user {:username "ubuntu"}]}

Options for your pushtoy config are:

* *:ips* - vector of deployment ip addresses
* *:user* - same keys that are available under pallet.core.user

:username

:public-key-path : path string to public key file

:private-key-path : path string to private key file

:public-key : public key as a string or byte array

:private-key : private key as a string or byte array

:passphrase : passphrase for private key

:password : ssh user password

:sudo-password : password for sudo (defaults to :password)

:sudo-user : the user to sudo to

:no-sudo : flag to not use sudo (e.g. when the user has root privileges).


defaults to username being root and using private and public keys as ~/.ssh/id_rsa and ~/.ssh/id_rsa.pub

* *:app-name* - defaults to the name of the project


### Basic Usage

    $ lein pushtoy commands...

The available commands are:

* *install*: installs java, runit, and create a service for your clojure application
* *deploy*: deploys your uberjar in a location that can be run from runit
* *start*: starts your deployed uberjar using runit
* *stop*: stops your application via runit
* *restart*: restarts your application via runit


### Initial deployment

    $ lein do uberjar, pushtoy install deploy restart

### Subsequent deployments

    $ lein do uberjar, pushtoy deploy restart

## Todo

- Add example plugin configurations
- Allow options for settings up nginx with virtual hosts
- integrate vhost stuff with something like http://freedns.afraid.org/


## License

Copyright © 2013 Adrian Smith

Distributed under the Eclipse Public License, the same as Clojure.
