import './assets/scss/base.scss';

import { createApp } from 'vue';
import { createPinia } from 'pinia';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
// import '@/assets/icon/iconfont.css';
import Dialog from './components/Dialog.vue';
import Cover from './components/Cover.vue';
import Avatar from './components/Avatar.vue';
import Table from './components/Table.vue';
import App from './App.vue';
import router from './router';
import VueCookies from 'vue-cookies';
import Verify from './utils/Verify';
import request from './utils/Request';
import Message from './utils/Message';
import { Api } from './utils/Api';
import Utils from './utils/Utils';

const app = createApp(App);
app.use(ElementPlus);
app.component('Dialog', Dialog);
app.component('Cover', Cover);
app.component('Avatar', Avatar);
app.component('Table', Table);
app.use(createPinia());
app.use(router);
app.config.globalProperties.VueCookies = VueCookies;
app.config.globalProperties.bodyMaxWidth = 2000;
app.config.globalProperties.bodyMinWidth = 1250;

app.config.globalProperties.Verify = Verify;
app.config.globalProperties.request = request;
app.config.globalProperties.Message = Message;
app.config.globalProperties.Api = Api;
app.config.globalProperties.Utils = Utils;
app.config.globalProperties.rowCategoryCount = 10;
app.config.globalProperties.imageThumbnailSuffix = '_thumbnail.jpg';

app.mount('#app');
