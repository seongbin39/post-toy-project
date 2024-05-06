let emailChecked = false;
function checkEmail() {
  let email = document.getElementById('email').value;
  if (!validateEmail(email)) {
    alert('이메일 형식이 올바르지 않습니다.')
    return false;
  }

  let data = {
    email: email
  }
  $.ajax({
    url: '/user/validation/email',
    type: 'POST',
    data: JSON.stringify(data),
    contentType: 'application/json',
  }).done(function (res) {
    if (res) {
      emailChecked = true;
      alert('사용 가능한 이메일입니다.');
    } else {
      alert('이미 사용중인 이메일입니다.');
    }
  }).fail(function () {
    alert('이메일 중복확인에 실패했습니다.');
  });
}

function validateEmail(email) {
  var reg = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
  return reg.test(String(email).toLowerCase());
}

function validateUsername(username) {
  // 4자 이상, 12자 이하
  var reg = /^[a-zA-Z0-9]{4,12}$/;
  return reg.test(String(username));
}
function passwordRule(password) {
  // 8자 이상, 대문자 또는 소문자, 숫자, 특수문자 포함
  var reg = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[!@#$%^&*(),.?":{}|<>]).+$/;
  return reg.test(String(password));

}

document.getElementById('signupForm').addEventListener('submit', function(e) {
  e.preventDefault();
  window.onbeforeunload = null;
  if (validationForm()) {
    document.getElementById('signupForm').submit();
  }
});

function validationForm() {
  let username = document.getElementById('username').value;
  let password = document.getElementById('password').value;
  let checkPassword = document.getElementById('checkPassword').value;

  if (!validateUsername(username)) {
    alert('사용자 이름은 4자 이상, 12자 이하로 입력해주세요.');
    return false;
  }

  if (!passwordRule(password)) {
    alert('비밀번호는 8자 이상, 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다.');
    return false;
  }

  if (password !== checkPassword) {
    alert('비밀번호가 일치하지 않습니다.');
    return false;
  }

  if (!emailChecked) {
    alert('이메일 중복확인을 해주세요.');
    return false;
  }

  return true;

}

document.getElementById('password').addEventListener('input', validatePassword);
document.getElementById('checkPassword').addEventListener('input', validatePassword);

let debounceTimeout;
function validatePassword(){
  clearTimeout(debounceTimeout);
  debounceTimeout = setTimeout(function() {
    var password = document.getElementById('password');
    var checkPassword = document.getElementById('checkPassword');
    var passwordMessage = document.getElementById('passwordMessage');

    if(password.value !== checkPassword.value) {
      passwordMessage.innerHTML = '비밀번호가 일치하지 않습니다.';
      passwordMessage.style.color = 'red';
    } else {
      passwordMessage.innerHTML = '비밀번호가 일치합니다.';
      passwordMessage.style.color = 'green';
    }
  }, 500); // 500ms delay
}

function cancel() {
  location.href = '/login';
}

// 작성중 새로고침 나가기 방지
window.onbeforeunload = function () {
  return "작성중인 내용이 있습니다. 정말로 나가시겠습니까?";
};