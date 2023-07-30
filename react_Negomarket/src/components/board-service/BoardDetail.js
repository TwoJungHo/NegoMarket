import React, { useEffect, useState } from 'react'

import ReactQuill from 'react-quill'
import KakaoMapTest from '../kakaoMapComponents/KakaoMapTest';
import { fetchFn } from '../../NetworkUtils';
import { Link, useParams } from 'react-router-dom';
import { API_URL } from '../../Constants';


const modules = {
  toolbar: false
};

function BoardDetail() {
  const [board, setBoard] = useState(null);
  const sellId = useParams().sellId;
  const previewImage = `${API_URL}/sell-service/img/${sellId}`
  
  const formatTimestamp = (timestamp) => {
    const date = new Date(timestamp);
    return date.toLocaleString();
  };


  useEffect(() => {
    fetchFn("GET", `${API_URL}/board-service/boards/${sellId}`)
        .then(data => {
          setBoard(data)
        
        })
        
}, [sellId])

    
function openPopup(path) {
  const newWindow = window.open(path, '_blank', 'width=560,height=700,noopener,noreferrer');
  if (newWindow) newWindow.opener = null;
}

  return (
    <div>
      
        { board !== null && <>
        {/* 제목 */}
        <p className='detailTitle'>{board.title}</p>
        <div>
          
        {/* 이미지 */}
        <div className='imageBox' style={{display: 'inline-block',verticalAlign: '-67px' ,height: 'auto', width: '41%', textAlign: 'center', margin: '5px 5px 0px 0px' }}>
       <img style={{ maxHeight: '200px' , maxWidth: '400px', display:'inline-block', marginLeft: '5%'}}
        src={previewImage? previewImage : '/img/nego1.png'} alt='preview' />
       </div>

        {/* 거래정보 */}
       <div style={{verticalAlign: 'center', display: 'inline-block', width: '450px', height: '210px', paddingInline: '3%', marginLeft: '1%', fontSize: 'larger'}}>
        
        판매자 : {board.sellInfoResponse.username} <button className='detailbutton' onClick={() => openPopup(`/chatwith/${board.sellInfoResponse.username}`)}><img src='/img/chatting.png' style={{widthd: '30px', height: '30px'}}/></button><br/>
        물품 : {board.sellInfoResponse.productName}<br/>
        가격 : {board.sellInfoResponse.price}<br/>
        작성일 : {formatTimestamp(board.sellInfoResponse.createAt)}<br/>
        수정일 : {formatTimestamp(board.sellInfoResponse.updateAt)}<br/>
        거래상태 : {board.sellInfoResponse.sellState}<br/>

        </div> 
       </div>


       
        {/* 지도 */}
        <p/>
        <div><KakaoMapTest longitude={board.sellInfoResponse.longitude} latitude={board.sellInfoResponse.latitude} /></div>
        
        {/* 에디터 */}
        <div ><ReactQuill
        
        value={board.htmlString} readOnly
        theme="snow"
        modules={modules}
        style={{ height : "400px", width : "883px", margin : "0px 0px 50px 0px"}}
      /></div></>}
       


        </div>
  )
}

export default BoardDetail