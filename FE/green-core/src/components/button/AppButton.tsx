import classNames from 'classnames';
import React, { useMemo } from 'react';
import styles from './AppButton.module.scss';

type PropsType = {
  text: string;
  textColor?: string;
  bgColor?: string;
  className?: string;
  handleClick: (params: any) => any;
};

export default function AppButton({ bgColor, textColor, text, handleClick, className }: PropsType) {
  const buttonColor = useMemo(() => {
    if (bgColor == 'main') return `${styles.main}`;
    else if (bgColor == 'mainLight') return `${styles.mainLight}`;
    else if (bgColor == 'second') return `${styles.second}`;
    else if (bgColor == 'secondLight') return `${styles.secondLight}`;
    else if (bgColor == 'light') return `${styles.light}`;
    else if (bgColor == 'thin') return `${styles.thin}`;
    else if (bgColor == 'title') return `${styles.title}`;
    else if (bgColor == 'titleLight') return `${styles.titleLight}`;
    else if (bgColor == 'danger') return `${styles.danger}`;
    else if (bgColor == 'like') return `${styles.like}`;
    else if (bgColor == 'white') return `${styles.white}`;
    else if (bgColor == 'black') return `${styles.black}`;
  }, [bgColor, styles]);

  // const buttonTextColor = useMemo(() => {
  //   if (textColor == 'main') return `${styles.mainText}`;
  //   else if (textColor == 'mainLight') return `${styles.mainLightText}`;
  //   else if (textColor == 'second') return `${styles.secondText}`;
  //   else if (textColor == 'secondLight') return `${styles.secondlightText}`;
  //   else if (textColor == 'light') return `${styles.lightText}`;
  //   else if (textColor == 'thin') return `${styles.thinText}`;
  //   else if (textColor == 'title') return `${styles.titleText}`;
  //   else if (textColor == 'titleLight') return `${styles.titleLightText}`;
  //   else if (textColor == 'danger') return `${styles.dangerText}`;
  //   else if (textColor == 'like') return `${styles.likeText}`;
  //   else if (textColor == 'white') return `${styles.whiteText}`;
  // }, [textColor, styles]);

  return (
    <div className={`${styles.container} ${buttonColor} ${className}`} onClick={handleClick}>
      {text}
    </div>
  );
}

AppButton.defaultProps = {
  text: '버튼',
  bgColor: 'main',
};
