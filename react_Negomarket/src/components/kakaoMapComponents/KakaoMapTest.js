import React, { useEffect } from 'react'
const { kakao } = window;
let marker;

function KakaoMapTest(props) {
  
    useEffect(() => {
      const container = document.getElementById("kmap");
      const options = { center: new kakao.maps.LatLng(props.latitude, props.longitude) };
      let kakaoMap = new kakao.maps.Map(container, options);
      marker = new kakao.maps.Marker({position: kakaoMap.getCenter()});
      marker.setMap(kakaoMap);
     
      
    }, []);
  
    return (
      
        <div id="kmap"  className="border" style={{ display : 'inline-block', width: "880px", height: "200px", marginBottom: '1.5%'}}></div>
    );
  }
  
export default KakaoMapTest