document.addEventListener('DOMContentLoaded', function () {
  var toastElList = [].slice.call(document.querySelectorAll('.toast'))
  var toastList = toastElList.map(function (toastEl) {
    return new bootstrap.Toast(toastEl)
  });
  toastList.forEach(toast => toast.show());
});
document.getElementById('cancelButton').addEventListener('click', function () {
  window.history.back();
});
document.getElementById('saveButton').addEventListener('click', function (e) {
  e.preventDefault();
  const formDate = new FormData(document.getElementById('postForm'));
  formDate.set('content', editor.getData());
  $.ajax({
    type: 'POST',
    url: '/admin/notice/write',
    data: formDate,
    processData: false,
    contentType: false,
    success: function (res) {
      window.onbeforeunload = null;
      // location.href = '/post/' + res.id;
    },
    error: function (err) {
      const error = err.responseJSON;
      let errMsgList = [];
      let errKeyList = [];
      for (const key in error) {
        errMsgList.push(error[key]);
        errKeyList.push(key);
      }
      showErrorModal(errMsgList, errKeyList)
    }
  });
});

function showErrorModal(errMsgList, errKeyList) {
  // 모달의 본문에 에러 메시지 설정
  const modalBody = document.querySelector('#errorModal .modal-body');
  let container = document.createElement('div');
  container.classList.add('container', 'justify-content-between', 'align-items-center', 'mb-3');
  container.id = 'msgContainer';
  modalBody.appendChild(container);
  for (let i in errMsgList) {
    const message = errMsgList[i];
    const key = errKeyList[i];
    const msg = document.createElement('p');
    msg.textContent = message;
    msg.dataset.key = key;
    container.appendChild(msg);
  }
  // 모달 열기
  var errorModal = new bootstrap.Modal(document.getElementById('errorModal'));
  errorModal.show();
}

document.getElementById('modal-close').addEventListener('click', closeErrorModal);

function closeErrorModal() {
  // 모달 닫기
  const modalBody = document.querySelector('#errorModal .modal-body');
  modalBody.innerHTML = '';
  var errorModal = bootstrap.Modal.getInstance(document.getElementById('errorModal'));
  errorModal.hide();
}

function addAttachment() {
  const limit = 5;
  const fileInputs = document.querySelectorAll('input[name="attachments"]');
  if (fileInputs.length >= limit) {
    alert('첨부파일은 최대 5개까지 가능합니다.');
    return;
  }
  const divElement = document.createElement('div');
  divElement.className = 'd-flex align-items-center my-3';
  const fileInput = document.createElement('input');
  fileInput.type = 'file';
  fileInput.name = 'attachments';
  fileInput.classList.add('form-control', 'w-auto');
  fileInput.addEventListener('change', function () {
    validateFile(fileInput);
  });
  const removeButton = document.createElement('button');
  removeButton.type = 'button';
  removeButton.classList.add('btn', 'btn-close', 'ms-2');
  removeButton.addEventListener('click', function () {
    this.parentElement.remove();
  });
  divElement.appendChild(fileInput);
  divElement.appendChild(removeButton);
  document.getElementById('file-inputs').appendChild(divElement);
}

function validateFile(fileInput) {
  const file = fileInput.files[0];
  const fileSize = file.size;
  const fileName = file.name;
  const fileExtension = fileName.split('.').pop();
  const allowedExtensions = ['jpg', 'jpeg', 'png', 'gif', 'pdf', 'doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx', 'hwp', 'zip', 'txt'];
  const maxFileSize = 10 * 1024 * 1024;
  if (!allowedExtensions.includes(fileExtension.toLowerCase())) {
    alert('허용되지 않는 파일 형식입니다.');
    fileInput.value = '';
    return;
  }
  if (fileSize > maxFileSize) {
    alert('파일 크기는 10MB를 넘을 수 없습니다.');
    fileInput.value = '';
    return;
  }
}

// 작성중 새로고침 나가기 방지
window.onbeforeunload = function () {
  return "작성중인 내용이 있습니다. 정말로 나가시겠습니까?";
};