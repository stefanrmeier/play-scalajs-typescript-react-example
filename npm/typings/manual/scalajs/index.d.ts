declare namespace Utils.Validator {
    function isValidEmail(s:string):boolean;
    function isValidPhone(s:string):boolean;
    function isValidDate(s:string):boolean;
    function maxLength(s:string, l:number):boolean;
    function minLength(s:string, l:number):boolean;
}