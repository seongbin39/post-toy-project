function setTheme(theme) {

  document.getElementsByTagName('html')[0].setAttribute('data-bs-theme', theme);
  document.getElementById('mode-toggle').innerHTML =
    theme === 'dark' ? ('<i id="dark-mode" class="fas fa-moon fa-2xl"></i>')
      : ('<i id="light-mode" class="fas fa-sun fa-xl"></i>')
  localStorage.setItem('theme', theme);
}

function colorMode() {
  // 현재 모드 확인
  let theme = document.getElementsByTagName('html')[0].getAttribute('data-bs-theme');

  // 모드 변경
  theme === 'dark' ? setTheme('light') : setTheme('dark');

  let modeToggle = document.getElementById('mode-toggle');
  modeToggle.classList.add('rotate');
  setTimeout(function () {
    modeToggle.classList.remove('rotate');
  }, 500); // 애니메이션 시간과 일치해야 합니다.
}

function checkMode() {
  let theme = localStorage.getItem('theme');
  theme === 'dark' ? setTheme('dark') : setTheme('light');
}

window.onload = checkMode;