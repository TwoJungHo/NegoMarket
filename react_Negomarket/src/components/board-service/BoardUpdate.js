import React, { useEffect, useRef, useState } from 'react'
import { fetch_General, fetch_multiForm } from '../../networkFns/fetchFns';

import ReactQuill from 'react-quill';

import { useParams } from 'react-router-dom';
import SellUpdate from '../sell-service/SellUpdate';
import KakaoMapUpdate from '../kakaoMapComponents/KaKaoMapUpdate';
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

function BoardUpdate() {

  const editor = useRef();
  const title = useRef();
  const [imgFile, setImgFile] = useState();
  const [productName, setProductName] = useState();
  const [price, setPrice] = useState();
  const [latitude, setLatitude] = useState();
  const [longitude, setLongitude] = useState();
  const [sellState, setSellState] = useState();

  const [board, setBoard] = useState(null);
  const sellId = useParams().sellId;

  const setPositionData = { setLatitude, setLongitude }
  const setSellData = { setImgFile, setProductName, setPrice, setSellState }


  useEffect(() => {
    fetch_General("GET", `${API_URL}/board-service/boards/${sellId}`)
      .then(data => {
        setBoard(data)
      })
  }, [sellId])

  function saveButtonClickHandler() {
    const formData = new FormData();
    const htmlString = editor.current.getEditor().root.innerHTML;
    const deltaString = JSON.stringify(editor.current.getEditor().getContents());
    const delta = JSON.parse(deltaString);
    console.log(editor.current.getEditor().getContents());
    console.log(delta);
    
    formData.append("title", title.current.value);
    formData.append("imgFile", imgFile);
    formData.append("productName", productName);
    formData.append("price", price);
    formData.append("latitude", latitude);
    formData.append("longitude", longitude);
    formData.append("sellState", sellState);
    formData.append("htmlString", htmlString);
    //formData.append("deltaString", deltaString);


    console.log(formData.get("title"));
    console.log(formData.get("imgFile"));
    console.log(formData.get("productName"));
    console.log(formData.get("price"));
    console.log(formData.get("latitude"));
    console.log(formData.get("longitude"));
    console.log(formData.get("sellState"));
    console.log(formData.get("htmlString"));

    fetch_multiForm("PUT", `${API_URL}/board-service/boards/${sellId}`, formData)

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







  return (<>
    {board !== null && <>
      <div><input defaultValue={board.title} className='border' style={{width: '750px', height: '30px', marginTop: '0.5%'}} ref={title}></input>
        <button className='insertButton' style={{ height: '38px', width: '80px', margin: '0px 0px 0px 8px' }} onClick={saveButtonClickHandler} >등록</button></div>

      <div >
        <SellUpdate sellId={sellId} getSellData={board.sellInfoResponse} setSellData={setSellData} /></div>

      <p />
      <div style={{ display: 'inline-block', width: '600px', height: '20px' }}><KakaoMapUpdate getPositionData={board.sellInfoResponse} setPositionData={setPositionData} /></div>
      <p />

      <div ><ReactQuill
        value={board.htmlString}
        ref={editor}
        theme="snow"
        modules={modules}
        style={{ height: "400px", width: "883px", margin: "0px 0px 50px 0px" }}
      /></div></>}

  </>
  )
}

export default BoardUpdate