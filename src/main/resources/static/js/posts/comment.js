// 댓글 작성 폼 이벤트 리스너 등록
document.getElementById('comment-form').addEventListener('submit', function (e) {
  e.preventDefault();
  const postId = document.getElementById('post-id').value;
  const userId = document.getElementById('user-id').value;
  const content = document.getElementById('comment-content').value;
  const data = {postId: postId, content: content, userId: userId};

  $.ajax({
    type: 'POST',
    url: '/api/comment',
    contentType: 'application/json',
    data: JSON.stringify(data)
  }).done(function () {
    window.location.reload();
  }).fail(function () {
    alert('댓글 작성에 실패했습니다.');
  });
});

// 댓글 수정 폼 생성
function createEditForm(commentId, content, type) {
  const div = document.createElement('div');
  div.classList.add('justify-content-between', 'align-items-center', 'my-2', 'text-end')

  const textArea = createElementWithAttributes('textarea', {
    'rows': '3',
    'name': 'content',
    'class': 'form-control col-auto mb-2'
  });
  textArea.innerText = content;

  const save = createElementWithAttributes('button', {
    'type': 'button',
    'class': 'btn btn-success col-auto',
    'id': 'saveBtn-' + type + commentId
  });
  save.innerText = '저장';

  const cancel = createElementWithAttributes('button', {
    'type': 'button',
    'class': 'btn col col-auto me-2',
    'id': 'cancelBtn-' + type + commentId
  });
  cancel.innerText = '취소';

  div.appendChild(textArea)
  div.appendChild(cancel)
  div.appendChild(save)

  return div;
}

// 댓글 수정
function modifyBtn(commentId) {

  let comment = document.getElementById('comment-content-' + commentId);
  const content = comment.innerText;
  const parent = comment.parentElement;
  const innerHTML = parent.innerHTML;

  // 수정 폼 생성
  const editForm = createEditForm(commentId, content, 'comment');
  parent.innerHTML = '';
  parent.appendChild(editForm);

  // 저장, 취소 버튼 이벤트 리스너 등록
  const saveBtn = editForm.querySelector('#saveBtn-comment' + commentId);
  saveBtn.addEventListener('click', function () {
    const data = {content: editForm.querySelector('textarea').value};
    fetch('/api/comment/' + commentId, {
      method: 'post',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(data)
    }).then(function (response) {
      if (response.ok) {
        window.location.reload();
      }
    });
  });

  const cancelBtn = editForm.querySelector('#cancelBtn-comment' + commentId);
  cancelBtn.addEventListener('click', function () {
    parent.innerHTML = innerHTML;
  });

}

/**
 * 댓글 삭제
 * @param commentId
 */
function deleteComment(commentId) {
  fetch('/api/comment/' + commentId, {
    method: 'delete'
  }).then(function (response) {
    if (response.ok) {
      window.location.reload();
    }
  })
}

/**
 * 대댓글 불러오기
 * @param commentId
 */
function getReply(commentId) {
  toggleReplyForm(commentId);
  $.ajax({
    type: 'GET',
    url: '/api/reply/' + commentId,
    dataType: "text",
  }).done(function (res) {
    $("#reply-" + commentId).append(res);
    delModalSet();
  }).fail(function () {
    alert('대댓글 불러오기에 실패했습니다.');
  });
}

/**
 * 대댓글 숨기기
 * @param commentId
 */
function hideReply(commentId) {
  toggleReplyForm(commentId);
  document.getElementById('reply-' + commentId).innerHTML = '';
}

function toggleReplyForm(commentId) {
  const hideBtn = document.getElementById('hideBtn-' + commentId);
  const showBtn = document.getElementById('replyBtn-' + commentId);
  const replyForm = document.getElementById('reply-form-' + commentId);
  hideBtn.classList.toggle('d-none');
  showBtn.classList.toggle('d-none');
  replyForm.classList.toggle('d-none');
}

// 댓글 삭제 모달
const deleteCommentModal = document.getElementById('deleteCommentModal');
if (deleteCommentModal) {
  deleteCommentModal.addEventListener('show.bs.modal', function (event) {
    const button = event.relatedTarget;
    const commentId = button.getAttribute('data-bs-comment-id');
    const deleteBtn = document.getElementById('delete-comment-btn');
    deleteBtn.addEventListener('click', function () {
      deleteComment(commentId)
    })
  })
}

// 댓글 입력창 높이 자동 조절
document.querySelectorAll('textarea').forEach((textarea) => {
  textarea.addEventListener('input', function () {
    var lines = textarea.value.split('\n').length;
    textarea.rows = Math.min(Math.max(lines, 2), 10);
  });
});