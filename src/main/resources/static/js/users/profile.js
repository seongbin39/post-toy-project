function createElementWithAttributes(type, attributes) {
  var element = document.createElement(type);
  for (var key in attributes) {
    element.setAttribute(key, attributes[key]);
  }
  return element;
}

// 이름 수정
function updateUserName(id) {
  console.log('updateUserName');

  var username = createElementWithAttributes('input', {
    'name': 'username',
    'type': 'text',
    'placeholder': '이름',
    'value': document.getElementById('username').innerText
  });
  username.classList.add('form-control', 'mb-2', 'w-50');
  var saveBtn = document.createElement('button');
  saveBtn.classList.add('btn', 'btn-success');
  saveBtn.innerText = '저장';
  saveBtn.addEventListener('click', function () {
    if (validateUserName(username.value)) {
      console.log('username:', username.value);
      const data = {
        username: username.value,
      }
      console.log('data:', data);
      $.ajax({
        type: 'POST',
        url: `/user/username/${id}`,
        contentType: 'application/json',
        data: JSON.stringify(data),
      }).done(function () {
        location.reload();
      }).fail(function () {
        alert('이름 변경에 실패했습니다.');
      });
    }
  });

  document.getElementById('username-area').innerHTML = '';
  document.getElementById('username-area').appendChild(username);
  document.getElementById('username-area').appendChild(saveBtn);

}

// 이름 유효성 검사
function validateUserName(username) {
  if (username.length < 2 || username.length > 20 || username === '') {
    alert('이름은 2자 이상 20자 이하로 입력해주세요.');
    return false;
  }
  if (!/^[a-zA-Z0-9가-힣]*$/.test(username)) {
    alert('이름은 영문, 숫자, 한글만 입력 가능합니다.');
    return false;
  }
  if (username.includes(' ')) {
    alert('이름에 공백을 포함할 수 없습니다.');
    return false;
  }
  return true;
}

// 프로필 이미지 업로드
function uploadImg(id) {
  var input = document.createElement('input');
  input.setAttribute('type', 'file');
  input.setAttribute('name', 'file');
  input.setAttribute('accept', 'image/*');
  input.classList.add('d-none');
  document.body.appendChild(input);

  input.addEventListener('change', function () {
    var file = this.files[0];
    var formData = new FormData();
    formData.append('file', file);
    formData.append('id', id);

    $.ajax({
      url: `/user/image/${id}`,
      type: 'POST',
      data: formData,
      processData: false,  // tell jQuery not to process the data
      contentType: false   // tell jQuery not to set contentType
    }).done(function () {
      location.reload();
    }).fail(function () {
      console.log('Failed to upload file');
      alert('이미지 업로드에 실패했습니다.')
    });
  });

  input.click();
}

// 프로필 이미지 삭제
function removeImg(id) {
  var form = document.createElement('form');
  form.setAttribute('method', 'post');
  form.setAttribute('action', '/user/remove/image');
  document.body.appendChild(form);
  form.submit();

  $.ajax({
    type: 'DELETE',
    url: `/user/image/${id}`,
  }).done(function () {
    location.reload();
  }).fail(function () {
    alert('이미지 삭제에 실패했습니다.')
  });
}

function deleteAccount() {
  if (confirm('정말 탈퇴하시겠습니까? \n탈퇴 후에는 복구가 불가능합니다.')) {
    $.ajax({
      type: 'POST',
      url: '/user/delete',
    }).done(function () {
      location.href = '/logout';
    }).fail(function () {
      alert('탈퇴에 실패했습니다.')
    });
  }
}

// 비밀번호 변경
function changePw() {
  let form = document.getElementById('changePwForm');
  const data = {
    email: document.getElementById('email').value,
    newPw: form.newPw.value,
    newPwConfirm: form.newPwConfirm.value
  }

  $.ajax({
    type: 'POST',
    url: '/user/change/password',
    contentType: 'application/json',
    data: JSON.stringify(data)
  }).done(function () {
    alert('비밀번호가 변경되었습니다.')
    location.reload();
  }).fail(function () {
    alert('비밀번호 변경에 실패했습니다.');
  });
}

// 인증메일 발송
function authEmail() {
  alert('인증번호를 발송하였습니다.');
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
    success: function () {
      document.getElementById('code-area').classList.remove('d-none');
      document.getElementById('auth-btn').innerText = '재전송';
    },
    error: function (error) {
      console.log(error)
      alert('인증번호 발송에 실패했습니다.');
    }
  })
}

// 인증번호 확인
function checkCode() {
  var authCode = document.getElementById('authCode').value;
  if (authCode === '') {
    alert('인증번호를 입력해주세요.');
    return;
  }
  var data = {
    email: document.getElementById('email').value,
    authCode: authCode.trim()
  }
  $.ajax({
    type: 'POST',
    url: '/user/find/password',
    data: JSON.stringify(data),
    contentType: 'application/json',
    success: function (data) {
      if (data === false) {
        alert('인증번호가 일치하지 않습니다.');
        return;
      }
      document.getElementById('footer').classList.remove('d-none');
      document.getElementById('changePwForm').classList.remove('d-none');
      document.getElementById('mailAuthForm').classList.add('d-none');
    },
    error: function () {
      alert('인증요청에 오류가 발생하였습니다.');
    }
  })
}