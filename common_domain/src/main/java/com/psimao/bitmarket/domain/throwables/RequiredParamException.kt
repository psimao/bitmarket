package com.psimao.bitmarket.domain.throwables

class RequiredParamException(param: String): Throwable("The following param is required by the interactor: $param")