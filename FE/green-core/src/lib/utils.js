import Toastify from 'toastify-js';
import message from '@/assets/message.json';
import toastifyCSS from '@/assets/toastify.json';

export function checkInputFormToast() {
  Toastify({
    text: message.CheckInputForm,
    duration: 1500,
    position: 'center',
    stopOnFocus: true,
    style: toastifyCSS.fail,
  }).showToast();
}

export function setTotalCount(params) {
  // let share = parseInt(payload / state.ytonny.yTonnySize);
  // let left = payload % state.ytonny.yTonnySize;
  // if (left == 0) state.ytonny.yTonnyListTotalCount = share;
  // else state.ytonny.yTonnyListTotalCount = share + 1;
}

export function setPaginate(page, size) {
  page += size;
  return [page, size];
}

export function setAlertMessage(mentionNickname, type) {
  if (type === 'ALERT_LIKE') return `${mentionNickname}님이 회원님의 댓글에 좋아요를 누르셨습니다`;
  else return `${mentionNickname}님이 회원님의 포스트에 댓글을 다셨습니다`;
}
