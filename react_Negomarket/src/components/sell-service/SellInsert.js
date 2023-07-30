import React, { useRef, useState } from 'react'

function SellInsert({ setSellData }) {


    const [previewImage, setPreviewImage] = useState(null);

    const imgFileInputRef = useRef();
    const productNameRef = useRef();
    const priceRef = useRef();
   

    function handleInputChange() {

        const file = imgFileInputRef.current.files[0];
        const productName = productNameRef.current.value;
        const price = priceRef.current.value;
        

        setSellData.setImgFile(file);
        setSellData.setProductName(productName);
        setSellData.setPrice(price);
        

        // 그림 파일 입력시 미리 보기 생성
        if (file) {
            const reader = new FileReader();

            // file 읽기가 완료될 경우 실행되는 콜백함수
            reader.onloadend = () => {
                setPreviewImage(reader.result);
            };

            // 선택된 파일을 읽어서 파일의 내용이 직접 포함된 URL로 변환
            reader.readAsDataURL(file);
        } else {
            setPreviewImage(null);
        }

    }


    return (
        <div>
            
                <div className='imageBox-lg' style={{margin: '3em 2em 2em 0em'}}><img style={{ height: '300px', width: '450px', }} src={previewImage ? previewImage : '/img/nego1.png'} alt='preview' /></div>
                <div className='components-inputs' style={{maxHeight: '300px', width: '550px', marginTop: '3em'}}>
                  
                    <input type="file" ref={imgFileInputRef} onChange={handleInputChange}></input><br/>
                    <input type="text" ref={productNameRef} onChange={handleInputChange} placeholder='상품명'></input><br/>
                    <input type="text" ref={priceRef} onChange={handleInputChange} placeholder='가격'></input><br/>
                           
                </div>
            

        </div>
    )
}

export default SellInsert