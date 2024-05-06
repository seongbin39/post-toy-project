function updateReplyBtn(replyId) {
  let reply = document.getElementById('reply-content-' + replyId);
  const content = reply.innerText;
  const parent = reply.parentElement;
  const innerHTML = parent.innerHTML;

  const editForm = createEditForm(replyId, content, 'reply');
  parent.innerHTML = '';
  parent.appendChild(editForm);

  // 저장 버튼 이벤트 리스너 등록
  const saveBtn = editForm.querySelector('#saveBtn-reply' + replyId);
  saveBtn.addEventListener('click', function () {
    const data = {
      id: replyId,
      content: editForm.querySelector('textarea').value,
      userId: document.getElementById('user-id').value
    };
    $.ajax({
      method: 'POST',
      url: '/api/reply/update/' + replyId,
      contentType: 'application/json',
      data: JSON.stringify(data),
    }).done(function () {
      location.reload();
    }).fail(function () {
      alert('댓글 수정에 실패했습니다.');
    });
  });

  // 취소 버튼 이벤트 리스너 등록
  const cancelBtn = editForm.querySelector('#cancelBtn-reply' + replyId);
  cancelBtn.addEventListener('click', function () {
    parent.innerHTML = innerHTML;
  });
}

function saveReply(commentId, taggedUserId) {
  const data = {
    content: document.getElementById('reply-form-' + commentId).querySelector('textarea').value,
    userId: document.getElementById('user-id').value,
    parentCommentId: commentId,
  };
  if (isTagged) {
    data.taggedUserId = taggedUserId;
  }
  console.log('data', data);
  $.ajax({
    method: 'POST',
    url: '/api/reply',
    contentType: 'application/json',
    data: JSON.stringify(data),
  }).done(function () {
    location.reload();
  }).fail(function () {
    alert('댓글 작성에 실패했습니다.');
  });
}

function deleteReply(replyId) {
  $.ajax({
    method: 'POST',
    url: '/api/reply/delete/' + replyId,
  }).done(function () {
    location.reload();
  }).fail(function () {
    alert('댓글 삭제에 실패했습니다.');
  });
}
let isTagged = false;
function replyTag(parentCommentId, username) {
  let tagUser = document.getElementById('tag-' + parentCommentId);
  tagUser.classList.remove('d-none');
  tagUser.innerText = '@ ' + username;
  isTagged = true;
}

// 대댓글 삭제 모달
function delModalSet() {
  const deleteModal = document.getElementById('deleteReplyModal');
  deleteModal.addEventListener('show.bs.modal', function (event) {
    const button = event.relatedTarget;
    const replyId = button.getAttribute('data-bs-reply-id');
    const deleteBtn = document.getElementById('delete-reply-btn');
    deleteBtn.addEventListener('click', function () {
      deleteReply(replyId);
    })
  })
}