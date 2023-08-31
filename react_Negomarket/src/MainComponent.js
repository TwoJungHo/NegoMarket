
import React, { useState, useEffect } from 'react';

function MainComponent() {
  const [ setCurrentImageIndex] = useState(0);
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
      
      <img src='/img/nego_main4.png' alt=''/>
      {/* <div>
        {imageNames.map((imageName, index) => (
        <><br /><img
            key={index}
            src={imageBaseUrl + imageName}
            alt={`Image ${index + 1}`}
            className={`scrolling-image ${index === currentImageIndex ? 'active' : ''}`}
            style={{width: '100%'}}
            /></>
        ))}
      </div> */}
      <div className="content">
        {/* 페이지 컨텐츠 */}
      </div>
    </div>
  );
}

export default MainComponent;
