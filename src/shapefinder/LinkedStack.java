/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefinder;

//************************************************************************************
//************************************************************************************
public class LinkedStack implements StackInterface, java.io.Serializable {
	private Node topNode;
	//********************************************************************************
	public LinkedStack() {
		topNode = null;
	}
	//********************************************************************************	
	public void push(Object newEntry) {
		Node newNode = new Node(newEntry, topNode);
		topNode = newNode;
	}
	//********************************************************************************
	public Object pop() {
		Object top = null;
		if (topNode != null) {
			top = topNode.getData(); 
			topNode = topNode.getNextNode(); 
		}
		return top;
	}
	//********************************************************************************
	public Object peek() {
		Object top = null;
		if (topNode != null)
			top = topNode.getData(); 
		return top;
	}
	//********************************************************************************
	public boolean isEmpty() {
		return topNode == null;
	}
	//********************************************************************************
	public void clear() {
		topNode = null;
	}
	//********************************************************************************
	//********************************************************************************
	private class Node implements java.io.Serializable {
		private Object data;
		private Node	next;
		//****************************************************************************
		private Node(Object dataPortion) {
			data = dataPortion;
			next = null;	
		}
		//****************************************************************************		
		private Node(Object dataPortion, Node nextNode) {
			data = dataPortion;
			next = nextNode;	
		}
		//****************************************************************************		
		private Object getData() {
			return data;
		}
		//****************************************************************************		
		private void setData(Object newData) {
			data = newData;
		}
		//****************************************************************************		
		private Node getNextNode() {
			return next;
		}
		//****************************************************************************		
		private void setNextNode(Node nextNode) {
			next = nextNode;
		}
		//****************************************************************************
	}
	//********************************************************************************
	//********************************************************************************
}
//************************************************************************************
//************************************************************************************
