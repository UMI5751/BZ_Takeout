function loginApi(data) {
  return $axios({
    'url': '/employee/login',
    'method': 'post',
    data
  })
}

//当访问 'url': '/employee/logout'的时候，会触发springboot的logout部分的逻辑
function logoutApi(){
  return $axios({
    'url': '/employee/logout',
    'method': 'post',
  })
}
