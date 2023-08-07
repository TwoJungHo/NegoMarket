export async function fetch_General(method, url, json_Obj) {
    let options = {
      method: method,
      headers: {"Content-Type": "application/json",},
    };
  
    const token = localStorage.getItem("jwt");
    
    if (token !== null && token.length > 0) {
      
      options.headers.Authorization = "Bearer " + token;
    
    }
  
    if (json_Obj) {
      options.body = JSON.stringify(json_Obj);
    }
  
    try {
      
      const response = await fetch(url, options);
  
      if (response.status === 403 || response.status === 401) {
        window.location.href = "/login";
        return null;
      }
  
      if (response.status !== 403 && response.status !== 401 && !response.ok) {
      
        throw new Error("fetch failed"); 
        
      }
  
      return await response.json();
    
    } 
    catch (error) {console.log(error.message);}
  }

  export async function fetch_multiForm(method, url, formData) {
    let options = {
      method: method,
      headers: {
        
      },
      body: formData
    };
  
    const token = localStorage.getItem("jwt");
    
    if (token !== null && token.length > 0) {
      
      options.headers.Authorization = "Bearer " + token;
    
    }
    
    try {
      
      const response = await fetch(url, options);
  
      if (response.status === 403 || response.status === 401) {window.location.href = "/login";}
  
      if (!response.ok) { throw new Error("fetch failed"); }
  
      //window.location.href = "/login"
    
    } catch (error) {console.log(error.message);}
  }

