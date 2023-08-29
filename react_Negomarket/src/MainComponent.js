/* eslint-disable react-hooks/exhaustive-deps */
/* eslint-disable jsx-a11y/img-redundant-alt */
import React, { useState, useEffect } from 'react';

function MainComponent() {
  const [currentImageIndex, setCurrentImageIndex] = useState(0);
  const imageNames = ['restore.jpg', 'negomain.png', 'preview.png']; // 이미지 파일명 배열
  const imageBaseUrl = '/img/'; // 이미지가 저장된 기본 경로
  const scrollThresholds = [0, 0.33, 0.66, 1]; // 스크롤 위치 임계값 배열

  const handleScroll = () => {
    const scrollPosition = window.scrollY / (document.body.offsetHeight - window.innerHeight);
    let newIndex = 0;
    for (let i = 0; i < scrollThresholds.length; i++) {
      if (scrollPosition < scrollThresholds[i]) {
        newIndex = i - 1;
        break;
      }
    }
    if (newIndex < 0) newIndex = 0;
    setCurrentImageIndex(newIndex);
  };

  useEffect(() => {
    window.addEventListener('scroll', handleScroll);
    return () => {
      window.removeEventListener('scroll', handleScroll);
    };
  }, []);

  return (
    <div>
      <div>
        {imageNames.map((imageName, index) => (
        <><br /><img
            key={index}
            src={imageBaseUrl + imageName}
            alt={`Image ${index + 1}`}
            className={`scrolling-image ${index === currentImageIndex ? 'active' : ''}`}
            style={{width: '100%'}}
            /></>
        ))}
      </div>
      <div className="content">
        {/* 페이지 컨텐츠 */}
      </div>
    </div>
  );
}

export default MainComponent;
