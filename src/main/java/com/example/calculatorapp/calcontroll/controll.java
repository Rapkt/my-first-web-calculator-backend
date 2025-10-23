package com.example.calculatorapp.calcontroll;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Stack;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/calculator")
@CrossOrigin(origins="http://localhost:4200")
public class controll {


    @GetMapping("result")
    // the main function
    public String readapi(@RequestParam(required = false) String expr){
        double number ;
        String[] expre = {"*","-","+","/","^","√","%"};
        Stack<Double> nums = new Stack<>();
        Stack<String> exp = new Stack<>();
        List<String> expresstion= input(expr);
        for(int i =0;i< expresstion.size();i++){
            String x = expresstion.get(i);
            switch(x){
                case "*":
                    if(exp.empty()){
                        exp.push(x);
                        continue;
                    }
                    //check if the power in the stack is higher than *
                    if(power(exp.peek())> power(x)){
                        // calculate the numbers already in the stack
                        cal( exp,nums);
                    }
                    exp.push(x);
                    continue;
                case "/":
                    //check if the exp stack is empty
                    if(exp.empty()){
                        //check of we are dividing by 0 or if there is nothing to divide with
                        if(i+1 > expresstion.size()-1 ||Double.parseDouble(expresstion.get(i+1)) == 0){
                            return "E";
                        }
                        exp.push(x);
                        continue;
                    }
                    if(power(exp.peek())> power(x)){
                        cal( exp,nums);
                    }
                    if(Double.parseDouble(expresstion.get(i+1)) == 0){
                        return "E";
                    }
                    exp.push(x);
                    continue;
                case "-":
                    //check if the next element in the expression is a number or an operator
                    boolean found=false;
                    for(int m =0; m < expre.length; m++ ){
                        if(i != 0){
                        if(expresstion.get(i-1).equals(expre[m])) {
                            found = true;
                            break;
                        }

                        }
                    }
                    if(exp.isEmpty()){
                        //check if the next element is sqrt so we get the sqrt then we push the number in negative
                        if(expresstion.get(i+1).equals("√")){
                            nums.push(-1 * Math.sqrt(Double.parseDouble(expresstion.get(i+2))));
                            // if the first number sqrt and in negative
                            if(i == 0){
                                i+=2;
                                continue;
                            }
                            i += 2;
                            // we pushed the number in negative so we switch the operator to +
                            exp.push("+");
                            continue;
                        }
                        // we push the number in negative then check if it was a sign or an operator
                        number = Double.parseDouble(expresstion.get(i+1));
                        nums.push((double)-1 * number);
                        if(i != 0){
                            exp.push("+");
                        }
                        i+=1;
                        continue;
                    }
                    // if the - is an operator not a sign
                    if(!found) {
                        // check if the power of the operator in the stack is grater than +
                        if (power("+") < power(exp.peek())) {
                            // calculate the numbers in stack
                            cal(exp, nums);
                        }
                        if (i + 2 < expresstion.size() && expresstion.get(i + 1).equals("√")) {
                            number = Math.sqrt(Double.parseDouble(expresstion.get(i + 2)));
                            i += 1;
                        } else {
                            number = Double.parseDouble(expresstion.get(i + 1));
                        }

                        if( i+2 < expresstion.size()-1 && expresstion.get(i+2).equals("^")){
                            nums.push(-1 * Double.parseDouble(expresstion.get(i+1)));
                            i++;
                            continue;
                        }
                        exp.push("+");
                        nums.push((double) -1 * number);
                    }
                    else {
                        nums.push(-1 * Double.parseDouble(expresstion.get(i + 1)));
                    }
                    i +=1;
                    continue;
                case"+":
                    if(exp.empty()){
                        exp.push(x);
                        continue;
                    }
                    if(power("+") < power(exp.peek())){
                        cal(exp,nums);
                    }
                    exp.push(x);
                    continue;
                case"^":
                    number = nums.pop();
                    nums.push(Math.pow(number,2));
                    i +=1;
                    continue;
                case "%":
                    if(exp.empty()){
                        exp.push("/");
                        nums.push(100.0);
                        continue;
                    }
                    if(power(exp.peek())> power(x)){
                        cal( exp,nums);
                    }
                    exp.push("/");
                    nums.push(100.0);
                    continue;
                case"√":
                    number = Double.parseDouble(expresstion.get(i+1));
                    nums.push(Math.sqrt(number));
                    i+=1;
                    continue;

            }
            nums.push(Double.parseDouble(x));
        }
        return resultcalculated(exp,nums);
    }
    // fucntion to read the input
    public List<String> input(String ex){
        // array list for input
        List<String> tokens = new ArrayList<>();
        // string to add in the array
        StringBuilder num = new StringBuilder();

        for (int i = 0; i < ex.length(); i++) {
            char c = ex.charAt(i);
            //if the char is dot continue it is still the same number
            if (Character.isDigit(c) || c == '.') {
                num.append(c);
            }
            //there is an operation put it in the the next array index
            else if (c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '^' || c == '√') {
                if (num.length() > 0) {
                    tokens.add(num.toString());
                    num = new StringBuilder();
                }
                tokens.add(String.valueOf(c));
            }
        }
        if(num.length() > 0){
            tokens.add(num.toString());
        }
        return tokens;
    }
    // function to return the result
    public String resultcalculated(Stack<String> exp , Stack<Double> nums){
        while(!exp.empty()){
            double num2 = nums.pop();
            double num1 = nums.pop();
            if(exp.isEmpty()){
                return nums.pop().toString();
            }
            String e = exp.pop();
            switch (e){
                case"+":
                    nums.push(num1+num2);
                    break;
                case"*":
                    nums.push(num1 * num2);
                    break;
                case"/":
                    nums.push(num1 / num2);
                    break;
                case"^":
                    nums.push(Math.pow(num1,num2));
                    break;
                case"%":
                    nums.push(num1 % num2);
            }

        }
        return nums.pop().toString();
    }
    //function to calculate the numbers in so we can continue with the expression
    public void cal( Stack<String> exp , Stack<Double> nums) {
        while (!exp.isEmpty()) {
            double num2 = nums.pop();
            double num1 = nums.pop();
            String e = exp.pop();
            switch (e) {
                case "+":
                    nums.push(num1 + num2);
                    break;
                case "*":
                    nums.push(num1 * num2);
                    break;
                case "/":
                    nums.push(num1 / num2);
                    break;
                case "%":
                    nums.push(num1 % num2);
            }
        }
    }
    //function to return the power
    public int power(String x){
        if(x.equals("+") || x.equals("-")){
            return 1;
        }
        if(x.equals("*")  || x.equals("%")){
            return 2;
        }
        if(x.equals("/")){
            return 3;
        }
        return 0;
    }
}
