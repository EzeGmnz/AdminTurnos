class TimeDivision {

    constructor(services_name, appointment) {
        this.services_name = services_name
        this.appointment = appointment;
    }

    toString() {
        let output = "";
        for (let index in this.appointment) {
            for (let service in this.appointment[index]) {
                output += this.services_name[service] + " " + this.appointment[index][service]["start"] + " ";
            }
        }
        return output;
    }

    getAppointment() {
        return this.appointment;
    }

    populateDiv(div) {
        var startLabel = document.createElement("label");
        startLabel.appendChild(document.createTextNode(this.toString()));
        div.appendChild(startLabel);
    }
}

class Selector {
    constructor(items, onClickListener) {
        this.div = document.createElement("div");
        this.div.classList.add();
        this.elements = [];
        for (let x = 0; x < items.length; x++) {
            let element = new Element(items[x]);
            this.elements.push(element);
            this.div.appendChild(element.getDiv());
            element.getDiv().id = x;
            element.getDiv().addEventListener("click", onClickListener, false);
        }
        this.selected = -1;
    }

    getDiv() {
        return this.div;
    }

    select(index) {
        if (this.selected == index) {
            this.selected = -1;
            this.elements[index].toggle();
        } else {
            if (this.selected != -1) {
                this.elements[this.selected].toggle();
            }
            this.selected = index;
            this.elements[index].toggle();
        }
    }

    getSelected() {
        if (this.selected != -1) {
            return this.elements[this.selected].getContent();
        }
        return null;
    }
}

class Element {
    constructor(content) {
        this.content = content;
        this.div = document.createElement("div");

        this.content.populateDiv(this.div);
        this.div.classList.add("element");
        this.div.classList.add("elementSelected");
        this.div.classList.toggle("elementSelected");
    }

    getDiv() {
        return this.div;
    }

    toggle() {
        this.div.classList.toggle("elementSelected");
    }

    getContent() {
        return this.content;
    }
}