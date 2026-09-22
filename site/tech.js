const viewport = document.querySelector('#canvas-viewport');
const world = document.querySelector('#canvas-world');
const zoomLabel = document.querySelector('#zoom-label');
const toast = document.querySelector('#canvas-toast');
const WORLD_WIDTH = 1680;
const WORLD_HEIGHT = 1000;
const screenCatalog = {
  mapa: ['img/mapa.png', 'Mapa · marcadores e filtros', 'Mapa principal do Cidade em Dia'],
  acompanhamento: ['img/acompanhamento.png', 'Acompanhamento · progresso e histórico', 'Tela de acompanhamento de uma reclamação'],
  analisesIA: ['img/analisesIA.png', 'Análises e IA · indicadores e fila crítica', 'Painel de análises e inteligência artificial'],
  inicial: ['img/inicial.png', 'Início · painel administrativo', 'Tela inicial do administrador'],
  login: ['img/login.png', 'Login · acesso por perfil', 'Tela de login do Cidade em Dia']
};

let zoom = 1;
let offsetX = 0;
let offsetY = 0;
let pointerStart = null;
let toastTimer;
const clamp = (value, min, max) => Math.min(max, Math.max(min, value));

function renderCanvas() {
  // Layout zoom keeps text glyphs sharp; a transform would scale a composited
  // bitmap of the whole board and soften its typography at fractional scales.
  world.style.zoom = String(zoom);
  world.style.left = `${offsetX}px`;
  world.style.top = `${offsetY}px`;
  zoomLabel.textContent = `${Math.round(zoom * 100)}%`;
}

function fitCanvas() {
  if (window.innerWidth < 620) {
    zoom = 0.62;
    offsetX = 12;
    offsetY = 12;
    renderCanvas();
    return;
  }
  const padding = window.innerWidth < 620 ? 20 : 34;
  zoom = Math.min(1, (viewport.clientWidth - padding) / WORLD_WIDTH, (viewport.clientHeight - padding) / WORLD_HEIGHT);
  offsetX = (viewport.clientWidth - WORLD_WIDTH * zoom) / 2;
  offsetY = (viewport.clientHeight - WORLD_HEIGHT * zoom) / 2;
  renderCanvas();
}

function zoomAt(nextZoom, pointX = viewport.clientWidth / 2, pointY = viewport.clientHeight / 2) {
  const updatedZoom = clamp(nextZoom, 0.35, 1.6);
  const worldX = (pointX - offsetX) / zoom;
  const worldY = (pointY - offsetY) / zoom;
  zoom = updatedZoom;
  offsetX = pointX - worldX * zoom;
  offsetY = pointY - worldY * zoom;
  renderCanvas();
}

function showToast(message) {
  toast.textContent = message;
  toast.classList.add('is-visible');
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => toast.classList.remove('is-visible'), 2100);
}

document.querySelector('#zoom-in').addEventListener('click', () => zoomAt(zoom * 1.15));
document.querySelector('#zoom-out').addEventListener('click', () => zoomAt(zoom / 1.15));
document.querySelector('#fit-board').addEventListener('click', fitCanvas);
viewport.addEventListener('wheel', (event) => {
  event.preventDefault();
  zoomAt(zoom * (event.deltaY < 0 ? 1.08 : 1 / 1.08), event.clientX, event.clientY);
}, { passive: false });

viewport.addEventListener('pointerdown', (event) => {
  if (event.target.closest('button, a')) return;
  pointerStart = { x: event.clientX, y: event.clientY, offsetX, offsetY };
  viewport.classList.add('is-dragging');
  viewport.setPointerCapture(event.pointerId);
});
viewport.addEventListener('pointermove', (event) => {
  if (!pointerStart) return;
  offsetX = pointerStart.offsetX + event.clientX - pointerStart.x;
  offsetY = pointerStart.offsetY + event.clientY - pointerStart.y;
  renderCanvas();
});
function stopDragging() {
  pointerStart = null;
  viewport.classList.remove('is-dragging');
}
viewport.addEventListener('pointerup', stopDragging);
viewport.addEventListener('pointercancel', stopDragging);

document.querySelector('#fullscreen-button').addEventListener('click', async () => {
  try {
    if (!document.fullscreenElement) await document.documentElement.requestFullscreen();
    else await document.exitFullscreen();
  } catch {
    showToast('Tela cheia não está disponível neste navegador.');
  }
});

document.querySelectorAll('.screen-tab').forEach((tab) => {
  tab.addEventListener('click', () => {
    const screen = screenCatalog[tab.dataset.screen];
    if (!screen) return;
    const image = document.querySelector('#active-screen');
    image.src = screen[0];
    image.alt = screen[2];
    document.querySelector('#screen-caption').textContent = screen[1];
    document.querySelector('#screen-open').href = screen[0];
    document.querySelectorAll('.screen-tab').forEach((item) => {
      const active = item === tab;
      item.classList.toggle('is-active', active);
      item.setAttribute('aria-selected', String(active));
    });
  });
});

document.addEventListener('keydown', (event) => {
  if (event.target.matches('input, textarea, select')) return;
  if (event.key === '+' || event.key === '=') zoomAt(zoom * 1.15);
  if (event.key === '-' || event.key === '_') zoomAt(zoom / 1.15);
  if (event.key === '0') fitCanvas();
  if (event.key.toLowerCase() === 'f') document.querySelector('#fullscreen-button').click();
});

window.addEventListener('resize', fitCanvas);
fitCanvas();
