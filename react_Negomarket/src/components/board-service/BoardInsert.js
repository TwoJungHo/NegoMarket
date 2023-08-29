import React, { useRef, useState } from 'react'
import SellInsert from '../sell-service/SellInsert'

import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';
import { fetch_multiForm } from '../../networkFns/fetchFns';
import KakaoMapBoardInsert from '../kakaoMapComponents/KakaoMapBoardInsert';
import { API_URL } from '../../Constants';

const modules = {
  toolbar: {
    container: [
      ['bold', 'italic', 'underline', 'strike'],
      ['link', 'image'],
    ],
    handlers: {},
  },
};

function BoardInsert() {

  const editor = useRef();
  const title = useRef();
  const [imgFile, setImgFile] = useState();
  const [productName, setProductName] = useState();
  const [price, setPrice] = useState();
  const [latitude, setLatitude] = useState();
  const [longitude, setLongitude] = useState();
  const [setSellState] = useState();

  const setPositionData = { setLatitude, setLongitude }
  const setSellData = { setImgFile, setProductName, setPrice, setSellState }

  function saveButtonClickHandler() {
    const formData = new FormData();
    const htmlString = editor.current.getEditor().root.innerHTML;
    const deltaString = JSON.stringify(editor.current.getEditor().getContents());
    const delta = JSON.parse(deltaString);
    if (
      title.current.value && imgFile && productName && price && latitude && longitude && htmlString
    ) {
      formData.append("title", title.current.value);
      formData.append("imgFile", imgFile);
      formData.append("productName", productName);
      formData.append("price", price);
      formData.append("latitude", latitude);
      formData.append("longitude", longitude);
      formData.append("sellState", "ON_SALE");
      formData.append("htmlString", htmlString);
      formData.append("deltaString", `[${JSON.stringify(delta.ops[0])}]`);
  
      fetch_multiForm("POST", `${API_URL}/board-service/boards`, formData)
      .then(data =>{
      window.location.href = `/board/detail/${data.sellId}`
      })
    } else {
      alert("모든 필수 항목을 작성해주세요")
      console.log("모든 필수 항목을 작성해주세요");
    }

  }

  const uploadImgCallBack = async (file) => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('filename', file.name);

    try {
      const response = await fetch(`${API_URL}/imgfile-service/uploadimg`, {
        method: 'POST',
        body: formData,
      });
      const data = await response.json();
      console.log(data.result)
      return `${API_URL}/imgfile-service/getimgdata?id=${data.result}`;
    } catch (error) {
      console.log(error);
    }
  };

  const imageHandler = () => {
    const quillEditor = editor.current.getEditor();
    const input = document.createElement('input');
    input.setAttribute('type', 'file');
    input.setAttribute('accept', 'image/*');
    input.click();

    input.onchange = async () => {
      const file = input.files[0];
      const url = await uploadImgCallBack(file);
      const range = quillEditor.getSelection(true);
      quillEditor.insertEmbed(range.index, 'image', url);
    };
  };
  modules.toolbar.handlers = { image: imageHandler }


  return (
    <div className='routes'>
      <div style={{width: '1080px', textAlign: 'center'}}>
      <input id='board-insert-title' placeholder='(*필수)제목' ref={title}></input>
      <button className = 'btn-lg' onClick={saveButtonClickHandler}>등록</button>
      </div>
        
      <div style={{width: '1080px'}}>
        <SellInsert setSellData={setSellData} />
      </div>

      <div>
        <KakaoMapBoardInsert setPositionData={setPositionData} />
      </div>

      <div>
        <ReactQuill
          ref={editor}
          theme="snow"
          modules={modules}
          id='board-insert-quill'
        />
      </div>

    </div>
  )
}

export default BoardInsert