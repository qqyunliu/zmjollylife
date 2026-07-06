

const regs = {
    email: /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/,
    password: /^[a-zA-Z0-9]{6,16}$/,


}
const Verify = (value, rule, regs, callback) => {
    if (value) {
        if (regs.test(value)) {
            callback();
        } else {
            callback(new Error(rule.message))
        }
    } else {
        callback();
    }
}

export default {
    email(rule, value, callback) {
        return Verify(value, rule, regs.email, callback);
    },
    password(rule, value, callback) {
        return Verify(value, rule, regs.password, callback);
    }
}
