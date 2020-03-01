# roove  [![GitHub license](https://img.shields.io/github/license/muramrr/roove)](https://github.com/muramrr/roove/blob/master/LICENSE) [![](https://img.shields.io/badge/SDK-v21+-BLUE.svg)](https://shields.io/)

A simple dating app based on tinder-style cards. WIP 91% done.


Used libraries/patterns:
* MVVM pattern;
* Dagger 2;
* RxJava 2;
* Glide;
* Firebase Auth;
* Firestore to store *user, messages* data;
* Firestorage to store *photos*;
* Data pagination;
* Retrofit to access public [Kudago](https://kudago.com) Api; 
* Facebook SDK to login.


![Logo](https://github.com/muramrr/roove/blob/master/media/roove_logo_256.png)


### Explanations

**So many developers, so many minds.**

Also, keep in mind, that *business* module should not contain android-based plugins. It is a pure kotlin module.

*Data* module is an android library.

*ViewModel* shoudn't contain any android imports, except androidx.lifecycle. 

*Log* classes in ViewModels for debug purposes only. They will be deleted after release.

MVVM pattern implementation in this project:

![MVVM](https://github.com/muramrr/roove/blob/master/media/arch_diagram.png)

## License

[GitHub license](https://github.com/muramrr/roove/blob/master/LICENSE)


```
Copyright (c) 2019 Andrii Kovalchuk
```
