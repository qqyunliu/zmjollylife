const getLocalImage=(image)=>{
    return new URL('../assets/${image}', import.meta.url).href;
}

export default{
    getLocalImage
}