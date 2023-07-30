import React, { useRef, useState } from 'react'
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';
import { fetch_multiForm } from '../../networkFns/fetchFns';
import GetRate from '../../GetRate';
import { useParams } from 'react-router-dom';
import { useEffect } from 'react';
import { fetchFn } from '../../NetworkUtils';
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

function ReviewInsert() {
  const sellId = useParams().sellId;
  const editor = useRef();
  const [score, setScore] = useState();
  const [review, setReview] = useState(null);

  function saveButtonClickHandler() {
    const formData = new FormData();
    const content = editor.current.getEditor().root.innerHTML;
    const deltaString = JSON.stringify(editor.current.getEditor().getContents());
    const delta = JSON.parse(deltaString);
    console.log(editor.current.getEditor().getContents());
    console.log(delta);
    formData.append("sellId", sellId);
    formData.append("content", content);
    formData.append("rate", score)
    formData.append("sellerName", review.sellInfoResponse.username);
    //formData.append("deltaString", deltaString);



    console.log(formData.get("sellId"));
    console.log(formData.get("content"));
    console.log(formData.get("rate"));
    console.log(formData.get("sellerName"));

    fetch_multiForm("POST", `${API_URL}/review-service/create`, formData)

  }

  useEffect(() => {
    fetchFn("GET", `${API_URL}/sell-service/board-sells/${sellId}`)
    .then(data => {
      setReview(data)
    })
  }, [])

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

      </div>
      { review !== null && <>
      <div>
        판매자 : {review.sellInfoResponse.username} <br/>
        물품 : {review.sellInfoResponse.productName}<br/>
        가격 : {review.sellInfoResponse.price}<br/>
      </div>
</>} 
<div>
      <div style={{display: 'inline-block'}}>
        <GetRate parentStateFunction={setScore}/>
      </div>
      <div style={{display: 'inline-block', verticalAlign: '25px', marginLeft: '15px'}}>
      <button className = 'btn-lg' onClick={saveButtonClickHandler}>등록</button></div></div>

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

export default ReviewInsert