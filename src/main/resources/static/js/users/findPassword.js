var mailAuth = false;
var totalSeconds = 5 * 60; // 5분

function countDown(sec) {
  var endTime = new Date().getTime() + sec * 1000; // 종료 시간을 현재 시간 + sec 초로 설정
  var countdownElement = document.getElementById('countdown');

  var countdownInterval = setInterval(function () {
    var now = new Date().getTime(); // 현재 시간
    var distance = endTime - now; // 종료 시간까지 남은 시간

    if (distance < 0) {
      clearInterval(countdownInterval);
      countdownElement.textContent = "시간 만료";
      document.getElementById('code-area').classList.add('d-none');
      return;
    }

    document.getElementById('code-area').classList.remove('d-none');

    var minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
    var seconds = Math.floor((distance % (1000 * 60)) / 1000);

    countdownElement.textContent = minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
  }, 1000);
}

function authEmail() {
  var email = document.getElementById('email').value;

  if (email === '' || email === null) {
    alert('이메일을 입력해주세요.');
    return;
  }
  if (!email.includes('@') || !email.includes('.')
    || email.length < 5 || email.indexOf('@') > email.lastIndexOf('.')
    || email.lastIndexOf('.') > email.length - 3) {
    alert('이메일 형식이 올바르지 않습니다.');
    return;
  }

  var data = {
    email: email
  }
  $.ajax({
    type: 'POST',
    url: '/user/auth/email',
    data: JSON.stringify(data),
    contentType: 'application/json',
  }).done(function () {
    document.getElementById('auth-btn').value = '재전송';
    mailAuth = true;
    countDown(5 * 60);
  }).fail(function () {
    alert('인증번호 발송에 실패했습니다.');
  })
}

function cancel() {
  location.href = '/login';
}

// 가입된 사용자 여부 확인
function checkUser() {
  var email = $('#email').val();
  var data = {
    email: email
  }
  if (email === '' || email === null) {
    alert('이메일을 입력해주세요.');
    return;
  }
  $.ajax({
    type: 'POST',
    url: '/user/validation/email',
    data: JSON.stringify(data),
    contentType: 'application/json',
  }).done(function (res) {
    if (!res) {
      authEmail();
    } else {
      alert('가입된 사용자가 아닙니다.');
    }
  }).error(function () {
    alert('조회 에러가 발생했습니다.')
  })
}

function setPassword() {
  if (!mailAuth) {
    alert('이메일 인증을 먼저 해주세요.');
    return;
  }
  var authCode = $('#authCode').val();
  if (authCode === '') {
    alert('인증번호를 입력해주세요.');
    return;
  }
  if (totalSeconds <= 0) {
    alert('인증시간이 만료되었습니다. 다시 인증을 진행해주세요.');
    return;
  }
  var data = {
    email: $('#email').val(),
    authCode: authCode.trim()
  }
  $.ajax({
    type: 'POST',
    url: '/user/find/password',
    data: JSON.stringify(data),
    contentType: 'application/json',
  }).done(function (res) {
    if (!res) {
      alert('인증번호가 일치하지 않습니다.');
      return;
    }
    document.getElementById('password-area').classList.remove('d-none');
    document.getElementById('changePwForm').classList.add('d-none');
  }).fail(function () {
    alert('인증요청에 오류가 발생하였습니다.');
  })
}

function extendTime() {
  if (totalSeconds <= 0) {
    alert('인증시간이 만료되었습니다. 다시 인증을 진행해주세요.');
    return;
  }
  totalSeconds = 5 * 60;
}

function resetPassword() {
  var email = document.getElementById('email').value;
  var password = document.getElementById('password').value;
  var confirmPassword = document.getElementById('confirm-password').value;
  if (password === '' || confirmPassword === '') {
    alert('비밀번호를 입력해주세요.');
    return;
  }
  if (password !== confirmPassword) {
    alert('비밀번호가 일치하지 않습니다.');
    return;
  }
  var data = {
    email: email,
    password: password
  }
  $.ajax({
    type: 'POST',
    url: '/user/reset/password',
    data: JSON.stringify(data),
    contentType: 'application/json',
  }).done(function (res) {
    if (!res) {
      alert('비밀번호 변경에 실패했습니다.');
      return;
    } else {
      alert('비밀번호가 변경되었습니다.');
      location.href = '/login';
    }
  }).fail(function () {
    alert('비밀번호 변경에 실패했습니다.');
  })
}