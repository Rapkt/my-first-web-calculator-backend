package com.example.calculatorapp.calcontroll;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Stack;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/calculator")
@CrossOrigin(origins="http://rapkt.github.io")
public class controll {


    @GetMapping("result")
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
                    if(power(exp.peek())> power(x)){
                        cal( exp,nums);
                    }
                    exp.push(x);
                    continue;
                case "/":
                    if(exp.empty()){
                        if(Double.parseDouble(expresstion.get(i+1)) == 0){
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
                        if(expresstion.get(i+1).equals("√")){
                            nums.push(-1 * Math.sqrt(Double.parseDouble(expresstion.get(i+2))));
                            if(i == 0){
                                i+=2;
                                continue;
                            }
                            i += 2;
                            exp.push("+");
                            continue;
                        }
                        number = Double.parseDouble(expresstion.get(i+1));
                        nums.push((double)-1 * number);
                        if(i != 0){
                            exp.push("+");
                        }
                        i+=1;
                        continue;
                    }
                    if(!found) {
                        if (power("+") < power(exp.peek())) {
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
                case"s":
                    number = nums.pop();
                    nums.push(Math.sqrt(number));
                    continue;
            }
            nums.push(Double.parseDouble(x));
        }
        return resultcalculated(exp,nums);
    }
    public List<String> input(String ex){
        List<String> tokens = new ArrayList<>();
        StringBuilder num = new StringBuilder();

        for (int i = 0; i < ex.length(); i++) {
            char c = ex.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                num.append(c);
            } else if (c == '+' || c == '-' || c == '*' || c == '/' || c == 's' || c == '%' || c == '^' || c == '√') {
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
