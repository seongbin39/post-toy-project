// 취소 버튼
document.getElementById('cancelButton').addEventListener('click', function () {
  // window.history.back();
  location.href = '/post/' + document.getElementById('postForm').dataset.postId;
});

function removeFile(fileId) {
  // 파일 요소를 찾습니다.
  var form = document.getElementById('postForm');
  var fileElement = document.getElementById('file-' + fileId);
  // 요소가 존재하면 삭제합니다.
  if (fileElement) {
    fileElement.remove();
    // 파일 삭제를 위한 hidden input을 생성합니다.
    var hiddenInput = document.createElement('input');
    hiddenInput.type = 'hidden';
    hiddenInput.name = 'deleteFiles';
    hiddenInput.value = fileId;
    form.appendChild(hiddenInput);
  }
}

function addAttachment() {
  const limit = 5;
  //기존 첨부파일 input 개수 확인
  let inputSize = 0;
  if (document.getElementById('files') != null) {
    inputSize = document.getElementById('files').childElementCount;
  }

  const fileInputs = document.querySelectorAll('input[name="attachments"]');
  if (fileInputs.length + inputSize >= limit) {
    alert('첨부파일은 최대 5개까지 가능합니다.');
    return;
  }
  const divElement = document.createElement('div');
  divElement.className = 'd-flex align-items-center my-3';
  const fileInput = document.createElement('input');
  fileInput.type = 'file';
  fileInput.name = 'attachments';
  fileInput.classList.add('form-control', 'w-auto');
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

// 저장 버튼 클릭시
document.getElementById('saveButton').addEventListener('click', function (e) {
  e.preventDefault();
  const form = document.getElementById('postForm');
  const formData = new FormData(form);
  // 에디터 내용을 formData에 추가
  const content = editor.getData();
  formData.set('content', content);

  $.ajax({
    type: 'POST',
    url: '/post/update/' + form.dataset.postId,
    data: formData,
    processData: false,
    contentType: false,
    success: function () {
      // 저장 버튼 클릭시 새로고침 나가기 방지 해제
      window.onbeforeunload = null;
      window.location.href = '/post/' + form.dataset.postId;
    },
    error: function (res) {
      alert(res.responseJSON.message);
    }
  })
});

// 작성중 새로고침 나가기 방지
window.onbeforeunload = function () {
  return "작성중인 내용이 있습니다. 정말로 나가시겠습니까?";
};
